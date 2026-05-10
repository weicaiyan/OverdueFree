package com.overduefree.module.lead.service;

import com.overduefree.module.lead.dto.SubmitLeadRequest;
import com.overduefree.module.lead.dto.SubmitLeadResult;

public interface LeadService {

    SubmitLeadResult submitLead(SubmitLeadRequest request);
}
