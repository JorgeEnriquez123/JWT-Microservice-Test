package com.jorge.bears.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Bear-Service Controllers doc",
                description = "Endpoints from Bear-service",
                version = "1.0"
        )
)
@Configuration
public class OpenApiConfig {
        @Bean
        public GroupedOpenApi apiV1() {
                return GroupedOpenApi.builder()
                        .group("v1")
                        .pathsToMatch("/bear/**")
                        .build();
        }

        /*@Bean
        public GroupedOpenApi apiV2() {
                return GroupedOpenApi.builder()
                        .group("v2")
                        .pathsToMatch("/api/v2/bear/**")
                        .build();
        }*/
        // In case there is another version of the controller
}
