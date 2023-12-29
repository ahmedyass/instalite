package com.instalite.instalite.service;

import com.instalite.instalite.dto.*;
import com.instalite.instalite.exception.InvalidRoleException;
import com.instalite.instalite.exception.UserAlreadyExistsException;
import com.instalite.instalite.exception.UserNotFoundException;
import com.instalite.instalite.model.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.instalite.instalite.repository.UserRepository;
import com.instalite.instalite.config.JwtService;
import com.instalite.instalite.model.User;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public void register(RegisterDto request) {
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
    }

    public AuthenticatedDto login(LoginDto request) {
        System.out.println(request.getUsername() + " " + request.getPassword());
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );
        System.out.println("Authenticated");
        var user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(UserNotFoundException::new);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticatedDto.builder()
            .jwt(jwtToken)
            .build();
    }

    public GetUserDto getById(UUID id) {
        var user = userRepository.findById(id)
            .orElseThrow(UserNotFoundException::new);
        return GetUserDto.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .role(user.getRole().name())
            .build();
    }

    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }

    public GetUserDto updateById(UUID id, EditUserDto request, String issuerUsername) {
        var user = userRepository.findById(id)
            .orElseThrow(UserNotFoundException::new);
        var issuer = userRepository.findByUsername(issuerUsername)
            .orElseThrow(UserNotFoundException::new);
        // Only admin can update other users
        if (!user.getId().equals(issuer.getId()) && !issuer.getRole().equals(UserRole.ADMINISTRATOR)) {
            // For safety reasons, we don't want to let user know if the user exists or not
            throw new UserNotFoundException();
        }

        if (request.getRole() != null && issuer.getRole().equals(UserRole.ADMINISTRATOR)) {
            try {
                user.setRole(UserRole.valueOf(UserRole.class, request.getRole()));
            } catch (IllegalArgumentException e) {
                throw new InvalidRoleException();
            }
        }
        if (request.getUsername() != null)
            user.setUsername(request.getUsername());
        if (request.getPassword() != null)
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        if (request.getEmail() != null)
            user.setEmail(request.getEmail());
        userRepository.save(user);
        return GetUserDto.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .role(user.getRole().name())
            .build();
    }

}
