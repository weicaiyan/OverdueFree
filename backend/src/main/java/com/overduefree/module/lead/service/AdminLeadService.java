package com.overduefree.module.lead.service;

import com.overduefree.common.PageResult;
import com.overduefree.module.lead.dto.AdminLeadQuery;
import com.overduefree.module.lead.dto.CustomerHistoryResult;
import com.overduefree.module.lead.dto.LeadListItem;

import java.util.List;

public interface AdminLeadService {

    PageResult<LeadListItem> listLeads(AdminLeadQuery query);

    List<LeadListItem> listLeadsForExport(AdminLeadQuery query);

    CustomerHistoryResult getCustomerHistory(Long customerId);
}
