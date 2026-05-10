package com.overduefree.module.admin.controller;

import com.overduefree.common.ApiResponse;
import com.overduefree.module.admin.dto.AdminMeResult;
import com.overduefree.module.admin.service.AdminAuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminMeController {

    private final AdminAuthService adminAuthService;

    public AdminMeController(AdminAuthService adminAuthService) {
        this.adminAuthService = adminAuthService;
    }

    @GetMapping("/me")
    public ApiResponse<AdminMeResult> me() {
        return ApiResponse.ok(adminAuthService.me());
    }
}
