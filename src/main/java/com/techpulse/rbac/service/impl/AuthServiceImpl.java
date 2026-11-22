package com.techpulse.rbac.service.impl;

import com.techpulse.rbac.dto.LoginRequest;
import com.techpulse.rbac.dto.LoginResponse;
import com.techpulse.rbac.entity.User;
import com.techpulse.rbac.exception.InvalidCredentialsException;
import com.techpulse.rbac.repository.UserRepository;
import com.techpulse.rbac.security.JwtService;
import com.techpulse.rbac.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log =
            LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .filter(User::isActive)
                .orElseThrow(() -> {
                    log.warn("Login failed: invalid or inactive user");
                    return new InvalidCredentialsException();
                });

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.warn("Login failed: incorrect password");
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(user.getUsername());
        log.info("Login successful");
        return new LoginResponse(token, "Bearer", jwtService.expirationMs());
    }
}
