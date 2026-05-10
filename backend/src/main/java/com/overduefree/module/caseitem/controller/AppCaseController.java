package com.overduefree.module.caseitem.controller;

import com.overduefree.common.ApiResponse;
import com.overduefree.common.PageResult;
import com.overduefree.module.caseitem.dto.SuccessCaseResult;
import com.overduefree.module.caseitem.service.SuccessCaseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app/cases")
public class AppCaseController {

    private final SuccessCaseService successCaseService;

    public AppCaseController(SuccessCaseService successCaseService) {
        this.successCaseService = successCaseService;
    }

    @GetMapping
    public ApiResponse<PageResult<SuccessCaseResult>> listPublished(@RequestParam(defaultValue = "1") Long page,
                                                                    @RequestParam(defaultValue = "10") Long pageSize) {
        return ApiResponse.ok(successCaseService.listPublished(page, pageSize));
    }

    @GetMapping("/{id}")
    public ApiResponse<SuccessCaseResult> getPublished(@PathVariable Long id) {
        return ApiResponse.ok(successCaseService.getPublished(id));
    }
}
