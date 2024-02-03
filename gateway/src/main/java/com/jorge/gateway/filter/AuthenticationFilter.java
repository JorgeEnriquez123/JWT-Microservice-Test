package com.jorge.gateway.filter;

import com.jorge.gateway.dto.UserDto;
import com.jorge.gateway.exception.AuthenticationException;
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
                        .uri("http://USER-SERVICE/api/v1/user/validateToken?token=" + jwtHeader)
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
                        .onErrorResume(
                                e -> {
                                    if (e instanceof WebClientResponseException && ((WebClientResponseException) e).getStatusCode() == HttpStatus.UNAUTHORIZED) {
                                        throw new AuthenticationException("Authentication Failed");
                                    }
                                    else {
                                        // Handle other exceptions differently
                                        throw new RuntimeException("Unexcepted error occurred", e);
                                    }
                                });
                        // Triggered if any error occurs during the process of WebClient,
                            // such as network error, JSON parsins error, or any exception thrown by the map()
                            // WebClient could throw a special Exception if requests have 4XX or 5XX code
            }
            return chain.filter(exchange);
        };
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
