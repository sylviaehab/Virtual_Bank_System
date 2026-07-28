package com.User_Service.service;


import com.User_Service.dto.*;
import com.User_Service.entity.User;
import com.User_Service.exception.DuplicateUserException;
import com.User_Service.exception.InvalidCredentialsException;
import com.User_Service.exception.UserNotFoundException;
import com.User_Service.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateUserException("Username already exists");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateUserException("Email already exists");
        }
        User user = User.builder()
                .username(request.username())
                .passwordHash(
                        passwordEncoder.encode(request.password())
                )
                .email(request.email())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .build();

        User savedUser = userRepository.save(user);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                "User registered successfully."
        );
    }


    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new InvalidCredentialsException(
                        "Invalid username or password."
                ));

        boolean passwordMatches = passwordEncoder.matches(
                request.password(),
                user.getPasswordHash()
        );

        if (!passwordMatches) {
            throw new InvalidCredentialsException(
                    "Invalid username or password."
            );
        }

        return new LoginResponse(
                user.getId(),
                user.getUsername()
        );
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "User with ID " + userId + " not found."
                ));

        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName()
        );
    }
}
