package com.overduefree.module.lead.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class LeadExportRequest {

    @Valid
    private AdminLeadQuery filters;

    @Size(max = 30)
    private List<String> fields;
}
