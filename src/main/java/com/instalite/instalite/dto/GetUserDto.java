package com.instalite.instalite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class GetUserDto {
    private UUID id;
    private String username;
    private String email;
    private String role;
}
