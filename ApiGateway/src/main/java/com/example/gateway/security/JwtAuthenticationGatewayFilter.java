//package com.example.gateway.security;
//
//import io.jsonwebtoken.Claims;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//public class JwtAuthenticationGatewayFilter
//{
//    @Component
//    public class JwtGatewayFilter implements GlobalFilter, Ordered {
//
//        private final JwtUtil jwtUtil; // simple helper you create
//
//        public JwtGatewayFilter(JwtUtil jwtUtil){ this.jwtUtil = jwtUtil; }
//
//        @Override
//        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//            ServerHttpRequest request = exchange.getRequest();
//            String path = request.getURI().getPath();
//
//            // Allow public auth endpoints
//            if (path.startsWith("/auth/")) return chain.filter(exchange);
//
//            String auth = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//            if (auth == null || !auth.startsWith("Bearer ")) {
//                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                return exchange.getResponse().setComplete();
//            }
//            String token = auth.substring(7);
//            try {
//                Claims claims = jwtUtil.parseToken(token);
//                String userId = String.valueOf(claims.get("userId"));
//                ServerHttpRequest mutated = request.mutate()
//                        .header("X-User-Id", userId)
//                        .header("X-User-Email", claims.getSubject())
//                        .build();
//                return chain.filter(exchange.mutate().request(mutated).build());
//            } catch (Exception e) {
//                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
//                return exchange.getResponse().setComplete();
//            }
//        }
//
//        @Override public int getOrder() { return -1; }
//    }
//
//}
