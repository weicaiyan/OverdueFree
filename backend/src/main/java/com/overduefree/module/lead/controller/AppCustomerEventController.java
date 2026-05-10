package com.overduefree.module.lead.controller;

import com.overduefree.common.ApiResponse;
import com.overduefree.module.lead.dto.CustomerEventRequest;
import com.overduefree.module.lead.service.CustomerEventService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/app/events")
public class AppCustomerEventController {

    private final CustomerEventService customerEventService;

    public AppCustomerEventController(CustomerEventService customerEventService) {
        this.customerEventService = customerEventService;
    }

    @PostMapping
    public ApiResponse<Void> createEvent(@Valid @RequestBody CustomerEventRequest request) {
        customerEventService.createEvent(request);
        return ApiResponse.ok();
    }
}
