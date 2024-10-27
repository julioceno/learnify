package com.learnify.sso.users.services;

import com.learnify.sso.common.exceptions.NotFoundException;
import com.learnify.sso.users.dto.UserDTO;
import com.learnify.sso.users.domain.User;
import com.learnify.sso.users.domain.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class FindUserByEmailService {
    private final Logger logger = LoggerFactory.getLogger(FindUserByEmailService.class.getName());

    @Autowired
    private UserRepository userRepository;

    public UserDTO run(String email) throws UsernameNotFoundException {
        logger.info(format("Finding user %s", email));
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            logger.info("User not exists");
            throw new NotFoundException(format("User with id %s not exists", email));
        }

        logger.info("Return user...");
        return new UserDTO(user);
    }
}
