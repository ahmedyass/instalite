package com.instalite.instalite.dto;

import lombok.Data;

@Data
public class EditUserDto {
    private String username;
    private String email;
    private String password;
    private String role;
}
