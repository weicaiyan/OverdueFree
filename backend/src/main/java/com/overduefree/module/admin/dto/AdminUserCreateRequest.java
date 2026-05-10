package com.overduefree.module.admin.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class AdminUserCreateRequest {

    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9_]{3,30}$")
    private String username;

    @NotBlank
    @Size(min = 6, max = 50)
    private String password;

    @NotBlank
    @Size(max = 50)
    private String displayName;

    @NotBlank
    @Pattern(regexp = "^(BOSS|ADMIN)$")
    private String role;
}
