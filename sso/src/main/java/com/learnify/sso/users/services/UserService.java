package com.learnify.sso.users.services;

import com.learnify.sso.users.dto.UserDTO;
import com.learnify.sso.users.dto.CreateUserDTO;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final CreateUserService createUserService;

    public UserService(CreateUserService createUserService) {
        this.createUserService = createUserService;
    }

    public UserDTO create(CreateUserDTO dto) {
        return createUserService.run(dto);
    }
}
