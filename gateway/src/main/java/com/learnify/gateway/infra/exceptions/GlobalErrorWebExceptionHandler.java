package com.learnify.gateway.infra.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.learnify.gateway.common.exceptions.StatusErrorException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Instant;

@Component
@Order(-2)
public class GlobalErrorWebExceptionHandler implements WebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        StatusErrorException error = (StatusErrorException) ex;
        HttpStatus status = error.getStatus();
        String message = error.getMessage();

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        StandardError standardError = new StandardError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                exchange.getRequest().getURI().getPath()
        );

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            byte[] jsonBytes = objectMapper.writeValueAsBytes(standardError);

            return exchange
                    .getResponse()
                    .writeWith(Mono.just(exchange.getResponse()
                    .bufferFactory().wrap(jsonBytes)));
        } catch (IOException e) {
            return Mono.error(e);
        }
    }
}
