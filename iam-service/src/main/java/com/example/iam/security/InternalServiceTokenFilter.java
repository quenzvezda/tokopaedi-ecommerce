package com.example.iam.security;

import com.example.iam.config.InternalAuthProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@Slf4j
@RequiredArgsConstructor
public class InternalServiceTokenFilter extends OncePerRequestFilter {
    private final InternalAuthProperties props;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("X-Internal-Token");
        if (header != null && header.equals(props.getServiceToken())) {
            AbstractAuthenticationToken auth = new AbstractAuthenticationToken(
                    List.of(new SimpleGrantedAuthority("ROLE_INTERNAL"))) {
                @Override public Object getCredentials() { return ""; }
                @Override public Object getPrincipal() { return "internal-service"; }
                @Override public boolean isAuthenticated() { return true; }
            };
            getContext().setAuthentication(auth);
        }
        chain.doFilter(request, response);
    }
}
