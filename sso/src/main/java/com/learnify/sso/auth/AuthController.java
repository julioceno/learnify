package com.learnify.sso.auth;

import com.learnify.sso.auth.dto.SignInUserDTO;
import com.learnify.sso.auth.dto.TokensDTO;
import com.learnify.sso.auth.services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping(value = "auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private HttpServletResponse response;

    @PostMapping("sign-in")
    public ResponseEntity signIn(@RequestBody @Valid SignInUserDTO signInUserDTO) {
        TokensDTO tokensDTO = authService.signIn(signInUserDTO);

        Cookie cookie = new Cookie("token", tokensDTO.token());
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        return ResponseEntity.noContent().build();
    }
}
