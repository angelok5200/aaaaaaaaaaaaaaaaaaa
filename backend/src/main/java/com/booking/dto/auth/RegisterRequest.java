package com.booking.dto.auth;

public record RegisterRequest (
        String name,
        String email,
        String password
) {
}
