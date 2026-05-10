package com.overduefree.module.lead.service;

import com.overduefree.common.PageResult;
import com.overduefree.module.lead.dto.AdminLeadQuery;
import com.overduefree.module.lead.dto.CustomerHistoryResult;
import com.overduefree.module.lead.dto.LeadListItem;

public interface AdminLeadService {

    PageResult<LeadListItem> listLeads(AdminLeadQuery query);

    CustomerHistoryResult getCustomerHistory(Long customerId);
}
