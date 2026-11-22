package com.techpulse.rbac.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.techpulse.rbac.entity.User;
import com.techpulse.rbac.repository.UserRepository;
import jakarta.servlet.FilterChain;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

class JwtAuthenticationFilterTest {

    private JwtService jwtService;
    private UserRepository userRepository;
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(
                "test-jwt-secret-key-with-at-least-32-characters",
                3600000L);
        userRepository = mock(UserRepository.class);
        filter = new JwtAuthenticationFilter(jwtService, userRepository);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void authenticatesActiveUserWithValidToken() throws Exception {
        String token = jwtService.generateToken("alice");
        User user = new User();
        user.setUsername("alice");
        user.setActive(true);
        when(userRepository.findByUsername("alice"))
                .thenReturn(Optional.of(user));

        MockHttpServletRequest request =
                new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response =
                new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        filter.doFilter(request, response, filterChain);

        assertNotNull(
                SecurityContextHolder.getContext()
                        .getAuthentication());
        assertEquals(
                "alice",
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getName());
    }

    @Test
    void doesNotAuthenticateInactiveUser() throws Exception {
        String token = jwtService.generateToken("alice");
        User user = new User();
        user.setUsername("alice");
        user.setActive(false);
        when(userRepository.findByUsername("alice"))
                .thenReturn(Optional.of(user));

        MockHttpServletRequest request =
                new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response =
                new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        filter.doFilter(request, response, filterChain);

        assertNull(
                SecurityContextHolder.getContext()
                        .getAuthentication());
    }

    @Test
    void ignoresInvalidToken() throws Exception {
        MockHttpServletRequest request =
                new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid-token");
        MockHttpServletResponse response =
                new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        filter.doFilter(request, response, filterChain);

        assertNull(
                SecurityContextHolder.getContext()
                        .getAuthentication());
    }
}
