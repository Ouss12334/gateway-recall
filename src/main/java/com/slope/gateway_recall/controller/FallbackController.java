package com.slope.gateway_recall.controller;

import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class FallbackController {

    @GetMapping("/fallback")
    public Mono<String> fallback() {
        return Mono.just("fallback");
    }
}
