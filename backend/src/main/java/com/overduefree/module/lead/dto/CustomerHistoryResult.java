package com.overduefree.module.lead.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CustomerHistoryResult {

    private CustomerInfo customer;
    private List<LeadHistoryItem> leads = new ArrayList<>();
    private List<EventHistoryItem> events = new ArrayList<>();
}
