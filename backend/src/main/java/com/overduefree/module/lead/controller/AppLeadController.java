package com.overduefree.module.lead.controller;

import com.overduefree.common.ApiResponse;
import com.overduefree.module.lead.dto.SubmitLeadRequest;
import com.overduefree.module.lead.dto.SubmitLeadResult;
import com.overduefree.module.lead.service.LeadService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/app/leads")
public class AppLeadController {

    private final LeadService leadService;

    public AppLeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @PostMapping
    public ApiResponse<SubmitLeadResult> submitLead(@Valid @RequestBody SubmitLeadRequest request) {
        return ApiResponse.ok(leadService.submitLead(request));
    }
}
