package com.booking.dto.auth;

public record LoginRequest(
        String email,
        String password
) {
}
