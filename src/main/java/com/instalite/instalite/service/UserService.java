package com.instalite.instalite.service;

import com.instalite.instalite.dto.LoginDto;
import com.instalite.instalite.dto.RegisterDto;
import com.instalite.instalite.dto.RegisterResponseDto;
import com.instalite.instalite.exception.UserAlreadyExistsException;
import com.instalite.instalite.model.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.instalite.instalite.repository.UserRepository;
import com.instalite.instalite.config.JwtService;
import com.instalite.instalite.model.User;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public RegisterResponseDto register(RegisterDto request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException();
        }
        var user = User.builder()
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .email(request.getEmail())
            .role(UserRole.USER)
            .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return RegisterResponseDto.builder()
            .jwt(jwtToken)
            .build();
    }

    public RegisterResponseDto login(LoginDto request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );
        var user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
        var jwtToken = jwtService.generateToken(user);
        return RegisterResponseDto.builder()
            .jwt(jwtToken)
            .build();
    }
}
