package com.jorge.gateway.handler;

import com.jorge.gateway.exception.AuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import javax.naming.ServiceUnavailableException;
import java.util.Map;

@Slf4j
@Component
@Order(-1)  // The lower, the higher the precedence || this class precedes NoHandlerFoundException (Available in Spring Web)
public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {
    // ResourceProperties is deprecated since Spring boot 2.5.7
    // using WebProperties now
    public GlobalErrorWebExceptionHandler(ErrorAttributes errorAttributes, WebProperties webProperties,
                                          ApplicationContext applicationContext, ServerCodecConfigurer serverCodecConfigurer) {
        super(errorAttributes, webProperties.getResources(), applicationContext);
        this.setMessageWriters(serverCodecConfigurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        ErrorAttributeOptions options = ErrorAttributeOptions.of(
                ErrorAttributeOptions.Include.MESSAGE);           // Return message of incoming Exception
        Throwable throwable = getError(request);
        HttpStatusCode httpStatus = determineHttpStatus(throwable);

        Map<String, Object> errorAttributes = getErrorAttributes(request, options);  // Set values of Exception + default ones to a Map<>

        errorAttributes.put("status", httpStatus.value());
        errorAttributes.remove("error");

        logException(throwable, httpStatus);

        return ServerResponse.status(httpStatus.value())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorAttributes));
                                    //.fromObject() <- deprecated
    }

    private HttpStatusCode determineHttpStatus(Throwable throwable) {
        if (throwable instanceof ResponseStatusException) {
            return ((ResponseStatusException) throwable).getStatusCode();
        }
        else if (throwable instanceof AuthenticationException) {
            return HttpStatus.UNAUTHORIZED;
        } else if (throwable instanceof WebClientResponseException.ServiceUnavailable) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    private void logException(Throwable throwable, HttpStatusCode httpStatus) {
        if (httpStatus == HttpStatus.INTERNAL_SERVER_ERROR) {
            log.error("Internal server error occurred: {}", throwable.getMessage(), throwable);
        } else if (httpStatus == HttpStatus.UNAUTHORIZED) {
            log.warn("Unauthorized request: {}", throwable.getMessage(), throwable);
        } else if (httpStatus == HttpStatus.SERVICE_UNAVAILABLE) {
            log.warn("Service unavailable: {}", throwable.getMessage(), throwable);
        } else {
            log.info("Error occurred: {}", throwable.getMessage(), throwable);
        }
    }
}