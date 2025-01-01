package com.hanghae.gatewayservice.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Map;

@Component
@Slf4j(topic = "gateway JWT필터")
public class JwtFilter extends AbstractGatewayFilterFactory<JwtFilter.Config> {

    private static SecretKey secretKey;

    public JwtFilter(@Value("${spring.jwt.secret.key}") String jwtSecret) {
        super(Config.class);
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return handleUnauthorized(response, "Invalid User Request.");
            }

            String authHeader = request.getHeaders().getOrEmpty(HttpHeaders.AUTHORIZATION).getFirst();
            if (!authHeader.startsWith("Bearer ")) {
                return handleUnauthorized(response, "Invalid Authorization header format.");
            }

            log.info(authHeader);
            String token = authHeader.substring(7);


            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(secretKey)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                log.info("JWT Claims: ");

                ServerHttpRequest.Builder mutatedRequest = request.mutate();
                for (Map.Entry<String, Object> entry : claims.entrySet()) {
                    String claimKey = "X-Claim-" + entry.getKey();
                    String claimValue = String.valueOf(entry.getValue());
                    mutatedRequest.header(claimKey, claimValue);
                    log.info("{}: {}", claimKey, claimValue);
                }

                request = mutatedRequest.build();
                exchange = exchange.mutate().request(request).build();

            } catch (Exception e) {
                log.error("JWT validation failed", e);
                return handleUnauthorized(response, "JWT validation failed: " + e.getMessage());
            }

            log.info("Custom PRE filter: request uri -> {}", request.getURI());
            log.info("Custom PRE filter: request id -> {}", request.getId());

            return chain.filter(exchange).then(Mono.fromRunnable(() ->{
                log.info("Custom POST filter: response status code -> {}", response.getStatusCode());
            }));
        };
    }

    private Mono<Void> handleUnauthorized(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json");
        String body = String.format("{\"error\": \"%s\", \"message\": \"%s\"}", HttpStatus.UNAUTHORIZED.getReasonPhrase(), message);
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes());
        return response.writeWith(Mono.just(buffer));
    }

    @Data
    public static class Config {
        private boolean preLogger;
        private boolean postLogger;
    }
}