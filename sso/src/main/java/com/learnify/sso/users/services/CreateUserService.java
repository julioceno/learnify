package com.learnify.sso.users.services;

import com.learnify.sso.common.exceptions.BadRequestException;
import com.learnify.sso.users.dto.UserDTO;
import com.learnify.sso.users.domain.User;
import com.learnify.sso.users.domain.UserRepository;
import com.learnify.sso.users.dto.CreateUserDTO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import static java.lang.String.format;

@Slf4j
@Service
public class CreateUserService {
    @Autowired
    private UserRepository userRepository;

    public UserDTO run(final CreateUserDTO dto) {
        verifyIfUserAlreadyExists(dto.getEmail());
        User userCreated = createUser(dto);

        return new UserDTO(userCreated);
    }

    private void verifyIfUserAlreadyExists(final String email) {
        log.info(format("Finding user with email %s", email));
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            log.info(format("User with email %s already exists", email));
            throw new BadRequestException("Usuário já existe");
        }
    }

    private User createUser(final CreateUserDTO dto) {
        String encryptedPassword = new BCryptPasswordEncoder().encode(dto.getPassword());

        User userToCreate = new User();
        userToCreate.setEmail(dto.getEmail());
        userToCreate.setName(dto.getName());
        userToCreate.setPassword(encryptedPassword);

        userRepository.save(userToCreate);
        log.info(format("User with email %s created", dto.getEmail()));

        return userToCreate;
    }
}
