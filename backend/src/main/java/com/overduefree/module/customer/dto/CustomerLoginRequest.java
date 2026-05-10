package com.overduefree.module.customer.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class CustomerLoginRequest {

    @NotBlank
    @Pattern(regexp = "^1[3-9]\\d{9}$")
    private String phone;

    @NotBlank
    @Pattern(regexp = "^\\d{6}$")
    private String code;
}
