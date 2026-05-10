package com.overduefree.module.admin.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class AdminPasswordResetRequest {

    @NotBlank
    @Size(min = 6, max = 50)
    private String password;
}
