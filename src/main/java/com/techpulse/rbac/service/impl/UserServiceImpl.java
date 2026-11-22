package com.techpulse.rbac.service.impl;

import com.techpulse.rbac.dto.CreateUserRequest;
import com.techpulse.rbac.dto.UserResponse;
import com.techpulse.rbac.entity.User;
import com.techpulse.rbac.exception.DuplicateUserException;
import com.techpulse.rbac.repository.UserRepository;
import com.techpulse.rbac.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log =
            LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserResponse create(CreateUserRequest request) {
        String username = request.getUsername().trim();
        if (userRepository.findByUsername(username).isPresent()) {
            log.warn("User creation denied: duplicate username");
            throw new DuplicateUserException();
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);

        User savedUser = userRepository.save(user);
        log.info("User created");
        return new UserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.isActive());
    }
}
