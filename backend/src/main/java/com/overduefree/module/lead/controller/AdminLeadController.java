package com.overduefree.module.lead.controller;

import com.overduefree.common.ApiResponse;
import com.overduefree.common.PageResult;
import com.overduefree.module.lead.dto.AdminLeadQuery;
import com.overduefree.module.lead.dto.CustomerHistoryResult;
import com.overduefree.module.lead.dto.LeadExportRequest;
import com.overduefree.module.lead.dto.LeadListItem;
import com.overduefree.module.lead.service.AdminLeadService;
import com.overduefree.module.lead.service.LeadExportService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/admin")
public class AdminLeadController {

    private final AdminLeadService adminLeadService;
    private final LeadExportService leadExportService;

    public AdminLeadController(AdminLeadService adminLeadService, LeadExportService leadExportService) {
        this.adminLeadService = adminLeadService;
        this.leadExportService = leadExportService;
    }

    @GetMapping("/leads")
    public ApiResponse<PageResult<LeadListItem>> listLeads(@ModelAttribute AdminLeadQuery query) {
        return ApiResponse.ok(adminLeadService.listLeads(query));
    }

    @GetMapping("/customers/{customerId}/history")
    public ApiResponse<CustomerHistoryResult> getCustomerHistory(@PathVariable Long customerId) {
        return ApiResponse.ok(adminLeadService.getCustomerHistory(customerId));
    }

    @PostMapping("/leads/export")
    public void exportLeads(@Valid @RequestBody(required = false) LeadExportRequest request,
                            HttpServletRequest servletRequest,
                            HttpServletResponse response) {
        leadExportService.exportLeads(request, servletRequest, response);
    }
}
