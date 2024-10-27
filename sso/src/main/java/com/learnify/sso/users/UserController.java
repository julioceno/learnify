package com.learnify.sso.users;

import com.learnify.sso.common.Util;
import com.learnify.sso.users.domain.User;
import com.learnify.sso.users.dto.CreateUserDTO;
import com.learnify.sso.users.dto.UserDTO;
import com.learnify.sso.users.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController()
@RequestMapping(value = "users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    private ResponseEntity create(
            // TODO: validar a questao da validação de DTO
            @Valid @RequestBody CreateUserDTO dto
    ) {
        final UserDTO userDTO = userService.create(dto);
        URI uri = Util.createUri(userDTO.getId());
        return ResponseEntity.created(uri).body(userDTO);
    }

    @GetMapping("me")
    private ResponseEntity getMe() {
        User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDTO userDTO = new UserDTO(authenticatedUser);
        return ResponseEntity.ok(userDTO);
    }
}
