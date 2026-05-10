package com.overduefree.module.admin.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class AdminUserUpdateRequest {

    @NotBlank
    @Size(max = 50)
    private String displayName;

    @NotBlank
    @Pattern(regexp = "^(BOSS|ADMIN)$")
    private String role;

    @NotBlank
    @Pattern(regexp = "^(ACTIVE|DISABLED)$")
    private String status;
}
