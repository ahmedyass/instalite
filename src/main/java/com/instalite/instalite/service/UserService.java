package com.instalite.instalite.service;

import com.instalite.instalite.config.JwtService;
import com.instalite.instalite.dto.*;
import com.instalite.instalite.exception.InvalidRoleException;
import com.instalite.instalite.exception.UserNotFoundException;
import com.instalite.instalite.model.Token;
import com.instalite.instalite.model.User;
import com.instalite.instalite.model.UserRole;
import com.instalite.instalite.repository.TokenRepository;
import com.instalite.instalite.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;

    // Authentication

    public void register(RegisterDto request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return;
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
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );
        var user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(UserNotFoundException::new);
        var jwtToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        savedUserTokens(user, jwtToken);
        return AuthenticatedDto.builder()
            .jwt(jwtToken)
            .build();
    }

    private void savedUserTokens(User user, String jwtToken) {
        var token = Token.builder()
            .user(user)
            .token(jwtToken)
            .expired(false)
            .revoked(false)
            .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    // User

    public PaginatedResultsDto<GetUserDto> paginatedUsers(int page, int size) {
        var users = userRepository.findAll(Pageable.ofSize(size).withPage(page));
        var paginatedResults = PaginatedResultsDto.from(users.map(user -> GetUserDto.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .role(user.getRole().name())
            .build()
        ));
        paginatedResults.setPage(users.getNumber());
        paginatedResults.setItemsPerPage(users.getNumberOfElements());
        paginatedResults.setItemCount(users.getTotalElements());
        paginatedResults.setPageCount(users.getTotalPages());
        return paginatedResults;
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

    @Transactional
    public void deleteById(UUID id) {
        tokenRepository.deleteAllByUserId(id);
        userRepository.deleteById(id);
    }

    public void updateById(UUID id, EditUserDto request, String issuerUsername) {
        var user = userRepository.findById(id)
            .orElseThrow(UserNotFoundException::new);
        var issuer = userRepository.findByUsername(issuerUsername)
            .orElseThrow(UserNotFoundException::new);
        // Only admin can update other users
        if (!user.getId().equals(issuer.getId()) && !issuer.getRole().equals(UserRole.ADMINISTRATOR)) {
            // For safety reasons, we don't want to let user know if the user exists or not
            return;
        }

        boolean invalidateTokens = false;
        if (request.getRole() != null && issuer.getRole().equals(UserRole.ADMINISTRATOR)) {
            try {
                user.setRole(UserRole.valueOf(UserRole.class, request.getRole()));
            } catch (IllegalArgumentException e) {
                throw new InvalidRoleException();
            }
            invalidateTokens = true;
        }
        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
            invalidateTokens = true;
        }
        if (request.getPassword() != null)
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        if (request.getEmail() != null)
            user.setEmail(request.getEmail());
        if (invalidateTokens) {
            revokeAllUserTokens(user);
            // TODO: Generate new token for user and send it
//            var jwtToken = jwtService.generateToken(user);
//            savedUserTokens(user, jwtToken);
        }
        userRepository.save(user);
    }

}
