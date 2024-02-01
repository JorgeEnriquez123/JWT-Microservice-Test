package com.jorge.gateway.filter;

import com.jorge.gateway.dto.UserDto;
import com.jorge.gateway.exception.FailedAuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

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
                    throw new FailedAuthenticationException("Missing Authorization Header");
                }
                String jwtHeader = extractJwtFromHeader(request);

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
                        .flatMap(chain::filter);
            }
            return chain.filter(exchange);
        };
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
