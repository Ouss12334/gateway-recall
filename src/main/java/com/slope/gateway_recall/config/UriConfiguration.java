package com.slope.gateway_recall.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties
@EnableConfigurationProperties(UriConfiguration.class) // enable configuration props
public class UriConfiguration {

    private String httpbinUrl = "http://httpbin.org:80";
}
