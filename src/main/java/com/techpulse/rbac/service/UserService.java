package com.techpulse.rbac.service;

import com.techpulse.rbac.dto.CreateUserRequest;
import com.techpulse.rbac.dto.UserResponse;

public interface UserService {
    UserResponse create(CreateUserRequest request);
}
