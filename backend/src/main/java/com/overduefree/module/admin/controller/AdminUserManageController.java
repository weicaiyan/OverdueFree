package com.overduefree.module.admin.controller;

import com.overduefree.common.ApiResponse;
import com.overduefree.module.admin.dto.AdminPasswordResetRequest;
import com.overduefree.module.admin.dto.AdminUserCreateRequest;
import com.overduefree.module.admin.dto.AdminUserResult;
import com.overduefree.module.admin.dto.AdminUserUpdateRequest;
import com.overduefree.module.admin.service.AdminUserManageService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserManageController {

    private final AdminUserManageService adminUserManageService;

    public AdminUserManageController(AdminUserManageService adminUserManageService) {
        this.adminUserManageService = adminUserManageService;
    }

    @GetMapping
    public ApiResponse<List<AdminUserResult>> listUsers() {
        return ApiResponse.ok(adminUserManageService.listUsers());
    }

    @PostMapping
    public ApiResponse<AdminUserResult> createUser(@Valid @RequestBody AdminUserCreateRequest request) {
        return ApiResponse.ok(adminUserManageService.createUser(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<AdminUserResult> updateUser(@PathVariable Long id,
                                                   @Valid @RequestBody AdminUserUpdateRequest request) {
        return ApiResponse.ok(adminUserManageService.updateUser(id, request));
    }

    @PostMapping("/{id}/password")
    public ApiResponse<Void> resetPassword(@PathVariable Long id,
                                           @Valid @RequestBody AdminPasswordResetRequest request) {
        adminUserManageService.resetPassword(id, request);
        return ApiResponse.ok();
    }
}
