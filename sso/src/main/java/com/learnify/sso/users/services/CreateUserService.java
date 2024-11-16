package com.learnify.sso.users.services;

import com.learnify.sso.common.exceptions.BadRequestException;
import com.learnify.sso.users.dto.UserDTO;
import com.learnify.sso.users.domain.User;
import com.learnify.sso.users.domain.UserRepository;
import com.learnify.sso.users.dto.CreateUserDTO;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.param.CustomerCreateParams;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class CreateUserService {
    private final UserRepository userRepository;
    private final StripeClient stripeClient;

    @Transactional
    public UserDTO run(final CreateUserDTO dto) {
        verifyIfUserAlreadyExists(dto.getEmail());
        Customer customer =createUserInStripe(dto);
        User userCreated = createUser(dto, customer);

        return new UserDTO(userCreated);
    }

    private void verifyIfUserAlreadyExists(final String email) {
        log.info("Finding user with email {}", email);
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            log.info("User with email {} already exists", email);
            throw new BadRequestException("Usuário já existe");
        }
    }

    private User createUser(final CreateUserDTO dto, Customer customer) {
        String encryptedPassword = new BCryptPasswordEncoder().encode(dto.getPassword());

        User userToCreate = new User();
        BeanUtils.copyProperties(dto, userToCreate);
        userToCreate.setPassword(encryptedPassword);
        userToCreate.setCustomerId(customer.getId());

        userRepository.save(userToCreate);
        log.info("User with email {} created", dto.getEmail());

        return userToCreate;
    }

    private Customer createUserInStripe(final CreateUserDTO dto) {
        try {
            log.info("Creating customer in stripe...");
            CustomerCreateParams customerCreateParams = CustomerCreateParams.builder()
                    .setEmail(dto.getEmail())
                    .setName(dto.getName())
                    .build();

            Customer customer = stripeClient.customers().create(customerCreateParams);
            log.info("Customer created");
            return customer;
        } catch (StripeException e) {
            log.info("Ocurred error when try create customer {} in stripe", dto.getEmail());
            throw new BadRequestException("Ocorreu um erro ao tentar criar o usuário");
        }
    }
}
