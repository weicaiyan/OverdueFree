package com.overduefree.module.lead.service.impl;

import com.overduefree.auth.CurrentCustomerContext;
import com.overduefree.auth.CustomerPrincipal;
import com.overduefree.module.lead.dto.SubmitLeadRequest;
import com.overduefree.module.lead.dto.SubmitLeadResult;
import com.overduefree.module.lead.entity.LeadRecord;
import com.overduefree.module.lead.mapper.LeadRecordMapper;
import com.overduefree.module.lead.service.LeadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LeadServiceImpl implements LeadService {

    private final LeadRecordMapper leadRecordMapper;

    public LeadServiceImpl(LeadRecordMapper leadRecordMapper) {
        this.leadRecordMapper = leadRecordMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SubmitLeadResult submitLead(SubmitLeadRequest request) {
        CustomerPrincipal principal = CurrentCustomerContext.getRequired();
        LeadRecord leadRecord = new LeadRecord();
        leadRecord.setCustomerId(principal.getCustomerId());
        leadRecord.setSource(request.getSource());
        leadRecord.setSurname(request.getSurname());
        leadRecord.setRegion(request.getRegion());
        leadRecord.setDebtAmount(request.getDebtAmount());
        leadRecord.setDebtType(request.getDebtType());
        leadRecord.setDebtDescription(request.getDebtDescription());
        leadRecord.setAgeRange(request.getAgeRange());
        leadRecord.setJobStatus(request.getJobStatus());
        leadRecord.setCreditStatus(request.getCreditStatus());
        leadRecord.setMonthlyIncomeRange(request.getMonthlyIncomeRange());
        leadRecord.setMonthlyExpenseRange(request.getMonthlyExpenseRange());
        leadRecordMapper.insert(leadRecord);

        SubmitLeadResult result = new SubmitLeadResult();
        result.setLeadId(leadRecord.getId());
        return result;
    }
}
