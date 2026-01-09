package com.example.gateway.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationGatewayFilter
        implements GlobalFilter, Ordered {

    private static final Logger log =
            LoggerFactory.getLogger(JwtAuthenticationGatewayFilter.class);

    @Override
    public int getOrder() {
        return -1; // run before routing
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {

        log.info("JwtAuthenticationGatewayFilter invoked");

        String path = exchange.getRequest().getURI().getPath();
        log.info("Request path: {}", path);

        if (path.startsWith("/auth")) {
            log.info("Skipping auth endpoint");
            return chain.filter(exchange);
        }

        String authHeader =
                exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        log.info("Authorization header received: {}", authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // TEMP: allow request (we’ll validate token next)
        return chain.filter(exchange);
    }
}