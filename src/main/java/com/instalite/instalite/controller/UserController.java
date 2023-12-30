package com.instalite.instalite.controller;

import com.instalite.instalite.dto.*;
import com.instalite.instalite.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Athentication

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterDto request) {
        userService.register(request);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticatedDto> login(@RequestBody LoginDto request) {
        return ResponseEntity.ok(userService.login(request));
    }

    // User

    @GetMapping
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<PaginatedResultsDto<GetUserDto>> getPaginatedUsers(@RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "10") int size) {

        PaginatedResultsDto<GetUserDto> searchResultsDto = userService.paginatedUsers(page, size);
        return ResponseEntity.ok(searchResultsDto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<GetUserDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        userService.deleteById(id);
        return ResponseEntity.status(204).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> updateById(@PathVariable UUID id,
                                           @RequestBody EditUserDto request,
                                           Principal principal) {
        userService.updateById(id, request, principal.getName());
        return ResponseEntity.ok(null);
    }
}
