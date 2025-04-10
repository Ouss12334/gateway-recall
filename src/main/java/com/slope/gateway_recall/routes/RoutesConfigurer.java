package com.slope.gateway_recall.routes;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.slope.gateway_recall.config.UriConfiguration;

@Configuration
public class RoutesConfigurer {

    @Bean
    public RouteLocator myRouteLocator(RouteLocatorBuilder builder, UriConfiguration uriConfiguration /* name of param must match bean name or will be err when injecting it here*/) {
        // config route to redirect to httpbin.org/get when requesting localhost/get and adding a hello header
        return builder.routes()
        .route(p -> p.path("/get")
            .filters(f -> f.addRequestHeader("hello", "world"))
            .uri(uriConfiguration.getHttpbinUrl()))
        // redirect all request coming from http://somereal.site to httpbin.org/delay/n api (n=seconds)
        // conf circuit breaker filter with .filters() add name (somecircuitname) + fallback +..
        .route(predicateSpec -> predicateSpec
            .host("*.somereal.site") 
            // returns 504 bcz of below circruit filters, add fallback to redirect to localhost/fallback api
            .filters(f -> f.circuitBreaker(conf -> 
                conf.setName("somecircuitname")
                .setFallbackUri("forward:/fallback")))
            .uri(uriConfiguration.getHttpbinUrl()))
        .build();
    }
}
