package com.learnify.order.common;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnify.order.common.dto.UserDTO;
import com.learnify.order.common.exception.BadRequestException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Util {

    public static UserDTO deserializeUser(HttpServletRequest request) {
        String userJson = request.getHeader("X-User");

        if (userJson == null) {
            throw new UnauthorizedException();
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            UserDTO user = objectMapper.readValue(userJson, UserDTO.class);
            return user;
        } catch (JsonProcessingException e) {
            log.error("Ocurred error when try read user json", e);
            throw new BadRequestException("Ocorreu um erro na aplicação, entre em contato com o suporte");
        }

    }
}
