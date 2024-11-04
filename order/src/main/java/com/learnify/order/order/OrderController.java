package com.learnify.order.order;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "signature")
public class OrderController {
    @Autowired
    private HttpServletRequest request;

    @PostMapping
    public ResponseEntity create(HttpServletRequest request) {
        String user = request.getHeader("X-User");
        /**
         * TODO:
         * 1. Obter id do usuario
         * 2. obter body
         */

    }
}
