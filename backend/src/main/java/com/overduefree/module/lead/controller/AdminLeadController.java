package com.overduefree.module.lead.controller;

import com.overduefree.common.ApiResponse;
import com.overduefree.common.PageResult;
import com.overduefree.module.lead.dto.AdminLeadQuery;
import com.overduefree.module.lead.dto.CustomerHistoryResult;
import com.overduefree.module.lead.dto.LeadListItem;
import com.overduefree.module.lead.service.AdminLeadService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/admin")
public class AdminLeadController {

    private final AdminLeadService adminLeadService;

    public AdminLeadController(AdminLeadService adminLeadService) {
        this.adminLeadService = adminLeadService;
    }

    @GetMapping("/leads")
    public ApiResponse<PageResult<LeadListItem>> listLeads(@ModelAttribute AdminLeadQuery query) {
        return ApiResponse.ok(adminLeadService.listLeads(query));
    }

    @GetMapping("/customers/{customerId}/history")
    public ApiResponse<CustomerHistoryResult> getCustomerHistory(@PathVariable Long customerId) {
        return ApiResponse.ok(adminLeadService.getCustomerHistory(customerId));
    }
}
