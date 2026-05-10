package com.overduefree.module.lead.service;

import com.overduefree.module.lead.dto.LeadExportRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface LeadExportService {

    void exportLeads(LeadExportRequest request, HttpServletRequest servletRequest, HttpServletResponse response);
}
