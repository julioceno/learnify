package com.learnify.order.common;


import com.learnify.order.common.dto.UserDTO;
import jakarta.servlet.http.HttpServletRequest;

public class Util {

    public static UserDTO deserializeUser(HttpServletRequest request) {
        String user = request.getHeader("X-User");

    }
}
