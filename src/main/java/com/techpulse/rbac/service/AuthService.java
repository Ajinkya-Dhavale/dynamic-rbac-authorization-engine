package com.techpulse.rbac.service;

import com.techpulse.rbac.dto.LoginRequest;
import com.techpulse.rbac.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
