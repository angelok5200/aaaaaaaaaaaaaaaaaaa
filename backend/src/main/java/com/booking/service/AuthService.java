package com.booking.service;

import com.booking.dto.auth.AuthResponse;
import com.booking.dto.auth.LoginRequest;
import com.booking.dto.auth.RegisterRequest;
import com.booking.entity.RoleEntity;
import com.booking.entity.UserEntity;
import com.booking.repository.RoleRepository;
import com.booking.repository.UserRepository;
import com.booking.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {

    UserRepository userRepository;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;
    AuthenticationManager authenticationManager;
    JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.findByEmail(req.email()).isPresent()) {
            throw new RuntimeException("Email already used");
        }

        UserEntity u = new UserEntity();
        u.setEmail(req.email());
        u.setName(req.name());
        u.setPassword(passwordEncoder.encode(req.password()));

        RoleEntity roleUser = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("ROLE_USER not found in DB"));
        u.getRoles().add(roleUser);

        userRepository.save(u);

        var userDetails = org.springframework.security.core.userdetails.User
                .withUsername(u.getEmail())
                .password(u.getPassword())
                .authorities(u.getRoles().stream().map(r -> new SimpleGrantedAuthority(r.getName())).toList())
                .build();

        return AuthResponse.bearer(jwtService.generateToken(userDetails));
    }

    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password())
        );

        UserEntity u = userRepository.findByEmail(req.email()).orElseThrow();

        var authorities = u.getRoles().stream()
                .map(RoleEntity::getName)
                .map(SimpleGrantedAuthority::new)
                .toList();

        var userDetails = org.springframework.security.core.userdetails.User
                .withUsername(u.getEmail())
                .password(u.getPassword())
                .authorities(authorities)
                .build();

        return AuthResponse.bearer(jwtService.generateToken(userDetails));
    }
}
