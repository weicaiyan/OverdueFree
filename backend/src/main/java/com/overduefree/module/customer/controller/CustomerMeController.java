package com.overduefree.module.customer.controller;

import com.overduefree.common.ApiResponse;
import com.overduefree.module.customer.dto.CustomerMeResult;
import com.overduefree.module.customer.service.CustomerAuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app")
public class CustomerMeController {

    private final CustomerAuthService customerAuthService;

    public CustomerMeController(CustomerAuthService customerAuthService) {
        this.customerAuthService = customerAuthService;
    }

    @GetMapping("/me")
    public ApiResponse<CustomerMeResult> me() {
        return ApiResponse.ok(customerAuthService.me());
    }
}
