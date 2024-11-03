package com.learnify.gateway.infra.filters;

import com.learnify.gateway.common.exceptions.UnauthorizedException;
import com.learnify.gateway.dto.UserDTO;
import com.learnify.gateway.infra.exceptions.StandardError;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Instant;

@Slf4j
@Component
public class ValidateTokenFilter implements GlobalFilter, Ordered {
    @Value("${api.sso.url}")
    private String ssoUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        HttpMethod method = exchange.getRequest().getMethod();

        if (isPublicRoute(path, method)) {
            return chain.filter(exchange);
        }

        boolean tokenIsValid = validateToken(exchange);
        if (!tokenIsValid) {
            return Mono.error(new UnauthorizedException());
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private Boolean isPublicRoute(String path, HttpMethod method) {
        return (path.equals("/auth/sign-in") && method == HttpMethod.POST)
                || (path.equals("/users") && method == HttpMethod.POST)
                || (path.equals("/users/me") && method == HttpMethod.GET);
    }


    private boolean validateToken(ServerWebExchange exchange) {
        HttpCookie cookie = exchange.getRequest().getCookies()
                .getFirst("token");

        if (cookie == null) {
            return false;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.COOKIE, "token=" + cookie.getValue());
            RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, URI.create(ssoUrl + "/users/me"));

            ResponseEntity<UserDTO> response = restTemplate.exchange(requestEntity, UserDTO.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                UserDTO user = response.getBody();
                log.info("User receveid: {}", user.toString());
                return true;
            }
        } catch (RestClientResponseException e) {
            log.info(
                "Occurred error when call sso, status: {}, message: {}",
                e.getStatusCode(),
                e.getMessage()
            );
        }

        return false;
    }
}

