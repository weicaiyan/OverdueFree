package com.overduefree.module.customer.controller;

import com.overduefree.common.ApiResponse;
import com.overduefree.module.customer.dto.CustomerLoginRequest;
import com.overduefree.module.customer.dto.CustomerLoginResult;
import com.overduefree.module.customer.dto.SendCodeRequest;
import com.overduefree.module.customer.dto.SendCodeResult;
import com.overduefree.module.customer.service.CustomerAuthService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/app/auth")
public class CustomerAuthController {

    private final CustomerAuthService customerAuthService;

    public CustomerAuthController(CustomerAuthService customerAuthService) {
        this.customerAuthService = customerAuthService;
    }

    @PostMapping("/send-code")
    public ApiResponse<SendCodeResult> sendCode(@Valid @RequestBody SendCodeRequest request) {
        return ApiResponse.ok(customerAuthService.sendCode(request));
    }

    @PostMapping("/login")
    public ApiResponse<CustomerLoginResult> login(@Valid @RequestBody CustomerLoginRequest request) {
        return ApiResponse.ok(customerAuthService.login(request));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        customerAuthService.logout();
        return ApiResponse.ok();
    }
}
