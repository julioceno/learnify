package com.learnify.sso.auth.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnify.sso.auth.dto.SubjectDTO;
import com.learnify.sso.common.exceptions.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ValidateTokenService {
    final private Logger logger = LoggerFactory.getLogger(ValidateTokenService.class.getName());

    @Value("{api.security.token.secret}")
    private String secret;

    public SubjectDTO run(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String subjectString = JWT.require(algorithm)
                    .withIssuer("learnify-sso")
                    .build()
                    .verify(token)
                    .getSubject();

            return new ObjectMapper().readValue(subjectString, SubjectDTO.class);
        } catch (JsonProcessingException exceptione) {
            logger.info("An error ocurred when try decode token");
            throw new UnauthorizedException();
        }
    }
}
