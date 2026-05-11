package com.overduefree.module.operationlog.controller;

import com.overduefree.common.ApiResponse;
import com.overduefree.common.PageResult;
import com.overduefree.module.operationlog.dto.OperationLogQuery;
import com.overduefree.module.operationlog.dto.OperationLogResult;
import com.overduefree.module.operationlog.service.OperationLogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/operation-logs")
public class AdminOperationLogController {

    private final OperationLogService operationLogService;

    public AdminOperationLogController(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    @GetMapping
    public ApiResponse<PageResult<OperationLogResult>> listLogs(@ModelAttribute OperationLogQuery query) {
        return ApiResponse.ok(operationLogService.listLogs(query));
    }
}
