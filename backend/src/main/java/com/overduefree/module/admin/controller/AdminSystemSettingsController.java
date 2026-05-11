package com.overduefree.module.admin.controller;

import com.overduefree.common.ApiResponse;
import com.overduefree.module.admin.dto.AdminSystemSettingsResult;
import com.overduefree.module.admin.dto.AdminSystemSettingsUpdateRequest;
import com.overduefree.module.admin.service.AdminSystemSettingsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/admin/settings")
public class AdminSystemSettingsController {

    private final AdminSystemSettingsService adminSystemSettingsService;

    public AdminSystemSettingsController(AdminSystemSettingsService adminSystemSettingsService) {
        this.adminSystemSettingsService = adminSystemSettingsService;
    }

    @GetMapping
    public ApiResponse<AdminSystemSettingsResult> getSettings() {
        return ApiResponse.ok(adminSystemSettingsService.getSettings());
    }

    @PutMapping
    public ApiResponse<AdminSystemSettingsResult> updateSettings(
        @Valid @RequestBody AdminSystemSettingsUpdateRequest request
    ) {
        return ApiResponse.ok(adminSystemSettingsService.updateSettings(request));
    }
}
