package com.example.auth.web.support;

import jakarta.validation.constraints.Min;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Validated
public class DummyErrorController {

    @GetMapping("/t/constraint")
    public String constraint(@RequestParam @Min(5) int n) {
        return "ok";
    }

    @GetMapping("/t/conflict")
    public String conflict() {
        throw new DataIntegrityViolationException("dup");
    }

    @GetMapping("/t/illegal")
    public String illegal() {
        throw new IllegalArgumentException("bad");
    }

    @GetMapping("/t/forbidden")
    public String forbidden() {
        throw new AccessDeniedException("nope");
    }

    @GetMapping("/t/spring")
    public String spring() {
        throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, "teapot");
    }
}
