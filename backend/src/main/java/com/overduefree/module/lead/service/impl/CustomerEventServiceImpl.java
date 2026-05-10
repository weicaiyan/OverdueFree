package com.overduefree.module.lead.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.overduefree.auth.CurrentCustomerContext;
import com.overduefree.auth.CustomerPrincipal;
import com.overduefree.common.BusinessException;
import com.overduefree.common.ErrorCode;
import com.overduefree.module.lead.dto.CustomerEventRequest;
import com.overduefree.module.lead.entity.CustomerEvent;
import com.overduefree.module.lead.mapper.CustomerEventMapper;
import com.overduefree.module.lead.service.CustomerEventService;
import org.springframework.stereotype.Service;

@Service
public class CustomerEventServiceImpl implements CustomerEventService {

    private final CustomerEventMapper customerEventMapper;
    private final ObjectMapper objectMapper;

    public CustomerEventServiceImpl(CustomerEventMapper customerEventMapper, ObjectMapper objectMapper) {
        this.customerEventMapper = customerEventMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public void createEvent(CustomerEventRequest request) {
        CustomerPrincipal principal = CurrentCustomerContext.getRequired();
        CustomerEvent event = new CustomerEvent();
        event.setCustomerId(principal.getCustomerId());
        event.setEventType(request.getEventType());
        event.setSourcePage(request.getSourcePage());
        event.setRefType(request.getRefType());
        event.setRefId(request.getRefId());
        event.setMetadataJson(serializeMetadata(request));
        customerEventMapper.insert(event);
    }

    private String serializeMetadata(CustomerEventRequest request) {
        if (request.getMetadata() == null || request.getMetadata().isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(request.getMetadata());
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "行为参数不正确");
        }
    }
}
