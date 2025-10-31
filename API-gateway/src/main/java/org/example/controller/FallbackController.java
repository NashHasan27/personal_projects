package org.example.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @RequestMapping("/fallback")
    @CircuitBreaker(name = "CircuitBreaker", fallbackMethod = "fallback")
    public ResponseEntity<String> fallback() {
        return new ResponseEntity<>("Service is temporarily unavailable.Please try again later,sorry for the inconvenience caused.",
                                    HttpStatus.SERVICE_UNAVAILABLE);
    }
}
