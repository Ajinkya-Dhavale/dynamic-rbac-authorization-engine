package com.techpulse.rbac.dto;

public record LoginResponse(String accessToken, String tokenType, long expiresIn) {
}
