package com.overduefree.module.caseitem.controller;

import com.overduefree.common.ApiResponse;
import com.overduefree.common.PageResult;
import com.overduefree.module.caseitem.dto.SuccessCaseQuery;
import com.overduefree.module.caseitem.dto.SuccessCaseResult;
import com.overduefree.module.caseitem.dto.SuccessCaseSaveRequest;
import com.overduefree.module.caseitem.service.SuccessCaseService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/admin/cases")
public class AdminCaseController {

    private final SuccessCaseService successCaseService;

    public AdminCaseController(SuccessCaseService successCaseService) {
        this.successCaseService = successCaseService;
    }

    @GetMapping
    public ApiResponse<PageResult<SuccessCaseResult>> listCases(@ModelAttribute SuccessCaseQuery query) {
        return ApiResponse.ok(successCaseService.listAdmin(query));
    }

    @PostMapping
    public ApiResponse<SuccessCaseResult> createCase(@Valid @RequestBody SuccessCaseSaveRequest request) {
        return ApiResponse.ok(successCaseService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<SuccessCaseResult> updateCase(@PathVariable Long id,
                                                     @Valid @RequestBody SuccessCaseSaveRequest request) {
        return ApiResponse.ok(successCaseService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCase(@PathVariable Long id) {
        successCaseService.delete(id);
        return ApiResponse.ok();
    }

    @PatchMapping("/{id}/publish")
    public ApiResponse<SuccessCaseResult> publishCase(@PathVariable Long id) {
        return ApiResponse.ok(successCaseService.publish(id));
    }

    @PatchMapping("/{id}/offline")
    public ApiResponse<SuccessCaseResult> offlineCase(@PathVariable Long id) {
        return ApiResponse.ok(successCaseService.offline(id));
    }
}
