package com.example.gateway.error;

import com.example.common.web.response.ApiErrorResponse;
import com.example.common.web.response.ErrorProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.cloud.gateway.support.TimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ErrorAttributes khusus Gateway agar semua error keluar dengan format seragam
 * (mengikuti ApiErrorResponse dari modul common-web).
 */
public class GatewayErrorAttributes extends DefaultErrorAttributes {

    private static final Logger log = LoggerFactory.getLogger(GatewayErrorAttributes.class);

    private final String serviceName;
    private final ErrorProps props;

    public GatewayErrorAttributes(String serviceName, ErrorProps props) {
        super();
        this.serviceName = serviceName;
        this.props = props;
    }

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Throwable ex = getError(request);
        ServerWebExchange exchange = request.exchange();

        HttpStatus status = resolveStatus(ex, exchange);
        String code = mapCode(status, ex);

        // Logging singkat (stacktrace penuh hanya untuk 5xx)
        if (status.is5xxServerError()) {
            log.error("[GW:{}] {} {} -> {} {} : {}",
                    code, request.method(), request.path(), status.value(), status.getReasonPhrase(),
                    (ex != null ? ex.toString() : "-"), ex);
        } else {
            log.warn("[GW:{}] {} {} -> {} {} : {}",
                    code, request.method(), request.path(), status.value(), status.getReasonPhrase(),
                    (ex != null ? ex.toString() : "-"));
        }

        // Kumpulkan info upstream (route & target)
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        Map<String, Object> upstream = new LinkedHashMap<>();
        if (route != null) {
            upstream.put("routeId", route.getId());
            upstream.put("target", route.getUri() != null ? route.getUri().toString() : null);
        }
        if (ex != null) upstream.put("exception", ex.getClass().getSimpleName());

        // Optional stacktrace (dev only): dikontrol via app.errors.verbose
        String details = null;
        if (props != null && props.isVerbose() && ex != null) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            details = sw.toString();
        }

        String requestId = resolveRequestId(exchange);

        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(Instant.now())
                .service(serviceName)
                .path(request.path())
                .method(request.method().name())
                .status(status.value())
                .code(code)
                .message(status.getReasonPhrase())
                .requestId(requestId)
                .upstream(upstream.isEmpty() ? null : upstream)
                .details(details)
                .build();

        // DefaultErrorWebExceptionHandler mengharapkan Map<String,Object>
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("timestamp", body.timestamp);
        map.put("service", body.service);
        map.put("path", body.path);
        map.put("method", body.method);
        map.put("status", body.status);
        map.put("code", body.code);
        map.put("message", body.message);
        map.put("requestId", body.requestId);
        if (body.upstream != null) map.put("upstream", body.upstream);
        if (body.details != null) map.put("details", body.details);
        return map;
    }

    /**
     * Menentukan HttpStatus final. Hindari "percaya" status OK default dari response.
     * Hanya gunakan status response jika sudah >= 400.
     */
    private HttpStatus resolveStatus(Throwable ex, ServerWebExchange exchange) {
        // 1) Jika pipeline sudah set status error, hormati itu
        HttpStatusCode sc = exchange.getResponse().getStatusCode();
        if (sc != null && sc.value() >= 400) {
            try { return HttpStatus.valueOf(sc.value()); }
            catch (Exception ignored) { /* lanjut fallback */ }
        }

        // 2) Mapping khusus Spring Cloud Gateway
        if (ex instanceof TimeoutException) {
            return HttpStatus.GATEWAY_TIMEOUT; // 504
        }
        if (ex instanceof NotFoundException nfe) {
            String msg = nfe.getMessage();
            // Tidak ada instance (Eureka/LoadBalancer): treat 503
            if (msg.contains("Unable to find instance")) {
                return HttpStatus.SERVICE_UNAVAILABLE; // 503
            }
            // Route memang tidak ada → 404
            return HttpStatus.NOT_FOUND;
        }

        // 3) Error umum
        if (ex instanceof ResponseStatusException rse) {
            return HttpStatus.valueOf(rse.getStatusCode().value());
        }
        if (isTimeout(ex)) return HttpStatus.GATEWAY_TIMEOUT;        // 504
        if (isConnect(ex)) return HttpStatus.SERVICE_UNAVAILABLE;     // 503

        // 4) default
        return HttpStatus.INTERNAL_SERVER_ERROR; // 500
    }

    /**
     * Mengubah status/error → code yang seragam.
     */
    private String mapCode(HttpStatus status, Throwable ex) {
        if (ex instanceof TimeoutException) return "upstream_timeout";
        if (ex instanceof NotFoundException nfe) {
            String msg = nfe.getMessage();
            return msg.contains("Unable to find instance")
                    ? "upstream_unavailable"
                    : "route_not_found";
        }
        return switch (status) {
            case BAD_GATEWAY -> "bad_gateway";
            case GATEWAY_TIMEOUT -> "upstream_timeout";
            case SERVICE_UNAVAILABLE -> "upstream_unavailable";
            case NOT_FOUND -> "route_not_found";
            default -> "gateway_error";
        };
    }

    /**
     * Ambil requestId dengan prioritas: header request → header response → MDC.
     */
    private String resolveRequestId(ServerWebExchange exchange) {
        String rid = exchange.getRequest().getHeaders().getFirst("X-Request-Id");
        if (rid == null || rid.isBlank()) {
            rid = exchange.getResponse().getHeaders().getFirst("X-Request-Id");
        }
        if (rid == null || rid.isBlank()) {
            rid = MDC.get("requestId");
        }
        return rid;
    }

    private boolean isTimeout(Throwable ex) {
        if (ex == null) return false;
        String cn = ex.getClass().getName();
        return (ex instanceof java.util.concurrent.TimeoutException)
                || cn.contains("ReadTimeout")
                || cn.contains("TimeoutException")
                || cn.contains("io.netty.handler.timeout");
    }

    private boolean isConnect(Throwable ex) {
        if (ex == null) return false;
        return (ex instanceof java.net.ConnectException)
                || (ex instanceof java.net.UnknownHostException)
                || (ex instanceof java.net.NoRouteToHostException)
                || ex.getClass().getName().contains("ConnectTimeout");
    }
}
