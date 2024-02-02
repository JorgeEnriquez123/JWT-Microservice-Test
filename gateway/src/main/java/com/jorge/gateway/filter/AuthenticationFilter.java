package com.jorge.gateway.filter;

import com.jorge.gateway.dto.UserDto;
import com.jorge.gateway.exception.FailedAuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
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
                    return onError(exchange, HttpStatus.UNAUTHORIZED);
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
                                e -> onError(exchange, HttpStatus.UNAUTHORIZED));
                        // Triggered if any error occurs during the process of WebClient,
                            // such as network error, JSON parsins error, or any exception thrown by the map()
                            // WebClient could throw a special Exception if requests have 4XX or 5XX code
            }
            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    private String extractJwtFromHeader(ServerHttpRequest request) {
        String authheader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (!authheader.startsWith("Bearer ")) {
            throw new FailedAuthenticationException("Invalid Authorization Header");
        }
        return authheader.substring(7);

    }

    public static class Config{

    }
}
