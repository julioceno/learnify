package com.learnify.sso.auth.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnify.sso.auth.dto.SubjectDTO;
import com.learnify.sso.common.exceptions.UnauthorizedException;
import com.learnify.sso.users.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import static java.lang.String.format;

@Service
public class GenerateTokenService {
    final private Logger logger = LoggerFactory.getLogger(GenerateTokenService.class.getName());

    @Value("{api.security.token.secret}")
    private String secret;

    @Value("{api.security.token.expireIn}")
    private String expireIn;

    public String run(User user) {
        SubjectDTO subjectDTO = new SubjectDTO(user.getEmail());

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String subjectJson = objectMapper.writeValueAsString(subjectDTO);

            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("learnify-sso")
                    .withSubject(subjectJson)
                    .withExpiresAt(this.generateExpirationDate())
                    .sign(algorithm);
        } catch (JsonProcessingException e) {
            logger.info(format("Não foi possível autenticar o usuário %s", user.getEmail()));
            throw new UnauthorizedException("Não foi possível autenticar o usuário");
        }
    };

    private Instant generateExpirationDate() {
        int expireIn = Integer.parseInt(this.expireIn);
        return Instant
                .now()
                .plus(Duration.ofSeconds(expireIn));
    }
}
