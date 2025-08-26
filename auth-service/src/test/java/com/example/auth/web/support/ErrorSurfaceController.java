package com.example.auth.web.support;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class ErrorSurfaceController {

    // Hanya POST JSON → kalau GET ke /t/echo => 405
    // Kalau Content-Type bukan JSON => 415
    // Kalau Accept: XML => 406
    @PostMapping(
            value = "/t/echo",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Map<String, Object> echo(@RequestBody Map<String, Object> body) {
        return body;
    }

    // Lempar RuntimeException → 500 (karena kita exclude GlobalExceptionHandler)
    @GetMapping("/t/boom")
    public String boom() {
        throw new RuntimeException("boom");
    }
}
