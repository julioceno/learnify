package com.learnify.sso.users;

import com.learnify.sso.common.Util;
import com.learnify.sso.users.dto.CreateUserDTO;
import com.learnify.sso.users.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;

@RestController()
@RequestMapping(value = "users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    private ResponseEntity create(
            @Valid @RequestBody CreateUserDTO dto
    ) {
        final UserDTO userDTO = userService.create(dto);
        URI uri = Util.createUri(userDTO.getId());
        return ResponseEntity.created(uri).body(userDTO);
    }
}
