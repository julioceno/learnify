package com.learnify.sso.auth.services;

import com.learnify.sso.auth.dto.SignInUserDTO;
import com.learnify.sso.auth.dto.TokensDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    final private SigInService sigInService;

    public TokensDTO signIn(SignInUserDTO dto) {
        return sigInService.run(dto);
    }
}
