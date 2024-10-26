package com.learnify.sso.auth.services;

import com.learnify.sso.auth.dto.SignInUserDTO;
import com.learnify.sso.auth.dto.TokensDTO;
import com.learnify.sso.users.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class SigInService {
    private final Logger logger = LoggerFactory.getLogger(SigInService.class);
    private final AuthenticationManager authenticationManager;
    private final GenerateTokenService generateTokenService;

    @Autowired
    public SigInService(AuthenticationManager authenticationManager, GenerateTokenService generateTokenService) {
        this.authenticationManager = authenticationManager;
        this.generateTokenService = generateTokenService;
    }

    public TokensDTO run(SignInUserDTO dto) {
        logger.info("Authenticate user...");
        var usernamePassword = new UsernamePasswordAuthenticationToken(dto.email(), dto.password());
        var auth = authenticationManager.authenticate(usernamePassword);
        User user = (User) auth.getPrincipal();

        logger.info("Generate token...");
        String token = generateTokenService.run(user);

        logger.info("Token generated, returning...");
        return new TokensDTO(token);
    }
}
