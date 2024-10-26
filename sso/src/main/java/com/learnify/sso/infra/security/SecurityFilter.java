package com.learnify.sso.infra.security;

import com.learnify.sso.auth.dto.SubjectDTO;
import com.learnify.sso.auth.services.ValidateTokenService;
import com.learnify.sso.users.domain.User;
import com.learnify.sso.users.domain.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
@AllArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {
    private final String tokenNameCookie = "token";
    private final ValidateTokenService validateTokenService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getToken(request);
        SubjectDTO subjectDTO = decodeToken(token);

        if (subjectDTO != null) {
            User user = userRepository.findByEmail(subjectDTO.email()).orElse(null);
            var authentication = new UsernamePasswordAuthenticationToken(user, null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        Optional<jakarta.servlet.http.Cookie> token = Arrays.stream(request.getCookies())
                .filter(item -> tokenNameCookie.equals(item.getName()))
                .findFirst();

        return token.map(Cookie::getValue).orElse(null);
    }

    private SubjectDTO decodeToken(String token) {
        try {
            return validateTokenService.run(token);
        } catch (RuntimeException e) {
            return null;
        }
    }
}
