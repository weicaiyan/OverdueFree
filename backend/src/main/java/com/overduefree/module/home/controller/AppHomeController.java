package com.overduefree.module.home.controller;

import com.overduefree.common.ApiResponse;
import com.overduefree.module.home.dto.HomeResult;
import com.overduefree.module.home.service.HomeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app/home")
public class AppHomeController {

    private final HomeService homeService;

    public AppHomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping
    public ApiResponse<HomeResult> getHome() {
        return ApiResponse.ok(homeService.getHome());
    }
}
