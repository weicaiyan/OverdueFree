package com.overduefree.module.lead.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.overduefree.common.BusinessException;
import com.overduefree.common.ErrorCode;
import com.overduefree.common.PageResult;
import com.overduefree.module.customer.entity.Customer;
import com.overduefree.module.customer.mapper.CustomerMapper;
import com.overduefree.module.lead.dto.AdminLeadQuery;
import com.overduefree.module.lead.dto.CustomerHistoryResult;
import com.overduefree.module.lead.dto.CustomerInfo;
import com.overduefree.module.lead.dto.EventHistoryItem;
import com.overduefree.module.lead.dto.LeadHistoryItem;
import com.overduefree.module.lead.dto.LeadListItem;
import com.overduefree.module.lead.entity.CustomerEvent;
import com.overduefree.module.lead.entity.LeadRecord;
import com.overduefree.module.lead.mapper.CustomerEventMapper;
import com.overduefree.module.lead.mapper.LeadAdminMapper;
import com.overduefree.module.lead.mapper.LeadRecordMapper;
import com.overduefree.module.lead.service.AdminLeadService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminLeadServiceImpl implements AdminLeadService {

    private static final long DEFAULT_PAGE = 1L;
    private static final long DEFAULT_PAGE_SIZE = 10L;
    private static final long MAX_PAGE_SIZE = 100L;

    private final LeadAdminMapper leadAdminMapper;
    private final CustomerMapper customerMapper;
    private final LeadRecordMapper leadRecordMapper;
    private final CustomerEventMapper customerEventMapper;

    public AdminLeadServiceImpl(LeadAdminMapper leadAdminMapper,
                                CustomerMapper customerMapper,
                                LeadRecordMapper leadRecordMapper,
                                CustomerEventMapper customerEventMapper) {
        this.leadAdminMapper = leadAdminMapper;
        this.customerMapper = customerMapper;
        this.leadRecordMapper = leadRecordMapper;
        this.customerEventMapper = customerEventMapper;
    }

    @Override
    public PageResult<LeadListItem> listLeads(AdminLeadQuery query) {
        AdminLeadQuery normalizedQuery = normalizeQuery(query);
        List<LeadListItem> rows = selectRows(normalizedQuery);

        long total = rows.size();
        long start = Math.min((normalizedQuery.getPage() - 1) * normalizedQuery.getPageSize(), total);
        long end = Math.min(start + normalizedQuery.getPageSize(), total);
        List<LeadListItem> pageRows = rows.subList((int) start, (int) end);
        return new PageResult<>(pageRows, normalizedQuery.getPage(), normalizedQuery.getPageSize(), total);
    }

    @Override
    public List<LeadListItem> listLeadsForExport(AdminLeadQuery query) {
        AdminLeadQuery normalizedQuery = normalizeQuery(query);
        return selectRows(normalizedQuery);
    }

    @Override
    public CustomerHistoryResult getCustomerHistory(Long customerId) {
        Customer customer = customerMapper.selectById(customerId);
        if (customer == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "客户不存在");
        }

        CustomerHistoryResult result = new CustomerHistoryResult();
        CustomerInfo customerInfo = new CustomerInfo();
        customerInfo.setCustomerId(customer.getId());
        customerInfo.setPhone(customer.getPhone());
        customerInfo.setStatus(customer.getStatus());
        customerInfo.setFirstLoginAt(customer.getFirstLoginAt());
        customerInfo.setLastLoginAt(customer.getLastLoginAt());
        result.setCustomer(customerInfo);

        List<LeadRecord> leads = leadRecordMapper.selectList(new LambdaQueryWrapper<LeadRecord>()
            .eq(LeadRecord::getCustomerId, customerId)
            .orderByDesc(LeadRecord::getCreatedAt));
        result.setLeads(leads.stream().map(this::toLeadHistoryItem).collect(Collectors.toList()));

        List<CustomerEvent> events = customerEventMapper.selectList(new LambdaQueryWrapper<CustomerEvent>()
            .eq(CustomerEvent::getCustomerId, customerId)
            .orderByDesc(CustomerEvent::getCreatedAt));
        result.setEvents(events.stream().map(this::toEventHistoryItem).collect(Collectors.toList()));
        return result;
    }

    private AdminLeadQuery normalizeQuery(AdminLeadQuery query) {
        AdminLeadQuery normalizedQuery = query == null ? new AdminLeadQuery() : query;
        long page = normalizedQuery.getPage() == null || normalizedQuery.getPage() < DEFAULT_PAGE
            ? DEFAULT_PAGE : normalizedQuery.getPage();
        long pageSize = normalizedQuery.getPageSize() == null || normalizedQuery.getPageSize() < DEFAULT_PAGE
            ? DEFAULT_PAGE_SIZE : Math.min(normalizedQuery.getPageSize(), MAX_PAGE_SIZE);
        normalizedQuery.setPage(page);
        normalizedQuery.setPageSize(pageSize);
        return normalizedQuery;
    }

    private boolean shouldIncludeLoginOnlyRows(AdminLeadQuery query) {
        if (AdminLeadQuery.TYPE_SUBMITTED.equals(query.getLeadType())) {
            return false;
        }
        return !StringUtils.hasText(query.getRegion())
            && !StringUtils.hasText(query.getDebtType())
            && query.getMinDebtAmount() == null
            && query.getMaxDebtAmount() == null;
    }

    private List<LeadListItem> selectRows(AdminLeadQuery query) {
        List<LeadListItem> rows = new ArrayList<>();
        if (!AdminLeadQuery.TYPE_LOGIN_ONLY.equals(query.getLeadType())) {
            rows.addAll(leadAdminMapper.selectSubmittedRows(query));
        }
        if (shouldIncludeLoginOnlyRows(query)) {
            rows.addAll(leadAdminMapper.selectLoginOnlyRows(query));
        }
        rows.sort(Comparator.comparing(this::resolveSortTime, Comparator.nullsLast(Comparator.reverseOrder())));
        return rows;
    }

    private LocalDateTime resolveSortTime(LeadListItem row) {
        if (row.getLeadCreatedAt() != null) {
            return row.getLeadCreatedAt();
        }
        return row.getFirstLoginAt();
    }

    private LeadHistoryItem toLeadHistoryItem(LeadRecord leadRecord) {
        LeadHistoryItem item = new LeadHistoryItem();
        BeanUtils.copyProperties(leadRecord, item);
        return item;
    }

    private EventHistoryItem toEventHistoryItem(CustomerEvent event) {
        EventHistoryItem item = new EventHistoryItem();
        BeanUtils.copyProperties(event, item);
        return item;
    }
}
