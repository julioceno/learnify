package com.learnify.sso.users.services;

import com.learnify.sso.common.exceptions.BadRequestException;
import com.learnify.sso.users.dto.UserDTO;
import com.learnify.sso.users.domain.User;
import com.learnify.sso.users.domain.UserRepository;
import com.learnify.sso.users.dto.CreateUserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import static java.lang.String.format;

@Service
public class CreateUserService {
    private final Logger logger = LoggerFactory.getLogger(CreateUserDTO.class.getName());

    @Autowired
    private UserRepository userRepository;

    public UserDTO run(final CreateUserDTO dto) {
        verifyIfUserAlreadyExists(dto.getEmail());
        User userCreated = createUser(dto);

        return new UserDTO(userCreated);
    }

    private void verifyIfUserAlreadyExists(final String email) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            logger.info(format("User with email %s already exists", email));
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
        logger.info(format("User with email %s created", dto.getEmail()));

        return userToCreate;
    }
}
