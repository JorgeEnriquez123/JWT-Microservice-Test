package com.jorge.gateway.filter;

import com.jorge.gateway.dto.UserDto;
import com.jorge.gateway.exception.AuthenticationException;
import com.jorge.gateway.exception.UserServiceNotAvailableException;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    private final RouteValidator routeValidator;
    private final WebClient.Builder webClientBuilder;

    public AuthenticationFilter(RouteValidator routeValidator, WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.routeValidator = routeValidator;
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (routeValidator.isSecured.test(request)) {
                if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new AuthenticationException("Authentication Failed");
                }
                String jwtHeader = extractJwtFromHeader(request);

                // JWT Validation could be done locally to reduce network latency
                return webClientBuilder.build()
                        .post()
                        .uri("http://USER-SERVICE/user/validateToken?token=" + jwtHeader)
                        .retrieve().bodyToMono(UserDto.class)
                        .map(userDto -> {
                            String userRolesAsString = String.join(",", userDto.getRoles());
                            exchange.getRequest()
                                    .mutate()
                                    .header("auth-user-username", userDto.getUsername())
                                    .header("auth-user-roles", userRolesAsString);
                            return exchange;
                        })
                        .flatMap(chain::filter)
                        .onErrorResume(WebClientResponseException.class, this::handleWebClientResponseException);
                        // Triggered if any error occurs during the process of WebClient,
                            // such as network error, JSON parsing error, or any exception thrown by the map()
                            // WebClient could throw a special Exception if requests have 4XX or 5XX code
            }
            return chain.filter(exchange);
        };
    }

    private Mono<Void> handleWebClientResponseException(WebClientResponseException e) {
        if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            return Mono.error(new AuthenticationException("Authentication Failed"));
        } else if (e.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
            return Mono.error(new UserServiceNotAvailableException("User service is not available for validation"));
        } else {
            return Mono.error(new RuntimeException("Unexpected error occurred", e));
        }
    }

    private String extractJwtFromHeader(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (!authHeader.startsWith("Bearer ")) {
            throw new AuthenticationException("Invalid Authorization Header");
        }
        return authHeader.substring(7);

    }

    public static class Config{

    }
}
