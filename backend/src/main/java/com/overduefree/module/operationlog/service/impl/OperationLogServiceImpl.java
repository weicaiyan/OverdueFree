package com.overduefree.module.operationlog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.overduefree.auth.AdminPrincipal;
import com.overduefree.auth.CurrentAdminContext;
import com.overduefree.common.BusinessException;
import com.overduefree.common.ErrorCode;
import com.overduefree.common.PageResult;
import com.overduefree.module.admin.entity.AdminUser;
import com.overduefree.module.operationlog.dto.OperationLogQuery;
import com.overduefree.module.operationlog.dto.OperationLogResult;
import com.overduefree.module.operationlog.entity.OperationLog;
import com.overduefree.module.operationlog.mapper.OperationLogMapper;
import com.overduefree.module.operationlog.service.OperationLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.stream.Collectors;

@Service
public class OperationLogServiceImpl implements OperationLogService {

    private static final Logger log = LoggerFactory.getLogger(OperationLogServiceImpl.class);
    private static final long DEFAULT_PAGE = 1L;
    private static final long DEFAULT_PAGE_SIZE = 10L;
    private static final long MAX_PAGE_SIZE = 100L;

    private final OperationLogMapper operationLogMapper;
    private final ObjectMapper objectMapper;

    public OperationLogServiceImpl(OperationLogMapper operationLogMapper, ObjectMapper objectMapper) {
        this.operationLogMapper = operationLogMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public void record(Long adminId, String action, String targetType, Long targetId, String ip, String userAgent, Object detail) {
        OperationLog operationLog = new OperationLog();
        operationLog.setAdminId(adminId);
        operationLog.setAction(action);
        operationLog.setTargetType(targetType);
        operationLog.setTargetId(targetId);
        operationLog.setIp(ip);
        operationLog.setUserAgent(userAgent);
        operationLog.setDetailJson(toJson(detail));
        operationLogMapper.insert(operationLog);
    }

    @Override
    public PageResult<OperationLogResult> listLogs(OperationLogQuery query) {
        ensureBoss();
        OperationLogQuery normalizedQuery = query == null ? new OperationLogQuery() : query;
        Page<OperationLog> pageRequest = new Page<>(
            normalizePage(normalizedQuery.getPage()),
            normalizePageSize(normalizedQuery.getPageSize())
        );
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<OperationLog>()
            .eq(normalizedQuery.getAdminId() != null, OperationLog::getAdminId, normalizedQuery.getAdminId())
            .eq(StringUtils.hasText(normalizedQuery.getAction()), OperationLog::getAction, normalizedQuery.getAction())
            .eq(
                StringUtils.hasText(normalizedQuery.getTargetType()),
                OperationLog::getTargetType,
                normalizedQuery.getTargetType()
            )
            .ge(normalizedQuery.getStartTime() != null, OperationLog::getCreatedAt, normalizedQuery.getStartTime())
            .le(normalizedQuery.getEndTime() != null, OperationLog::getCreatedAt, normalizedQuery.getEndTime())
            .orderByDesc(OperationLog::getCreatedAt)
            .orderByDesc(OperationLog::getId);
        Page<OperationLog> resultPage = operationLogMapper.selectPage(pageRequest, wrapper);
        return new PageResult<>(
            resultPage.getRecords().stream().map(this::toResult).collect(Collectors.toList()),
            resultPage.getCurrent(),
            resultPage.getSize(),
            resultPage.getTotal()
        );
    }

    private String toJson(Object detail) {
        if (detail == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(detail);
        } catch (JsonProcessingException e) {
            log.warn("操作日志详情序列化失败", e);
            return null;
        }
    }

    private AdminPrincipal ensureBoss() {
        AdminPrincipal principal = CurrentAdminContext.getRequired();
        if (!AdminUser.ROLE_BOSS.equals(principal.getRole())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "只有老板账号可以查看操作日志");
        }
        return principal;
    }

    private OperationLogResult toResult(OperationLog operationLog) {
        OperationLogResult result = new OperationLogResult();
        result.setId(operationLog.getId());
        result.setAdminId(operationLog.getAdminId());
        result.setAction(operationLog.getAction());
        result.setTargetType(operationLog.getTargetType());
        result.setTargetId(operationLog.getTargetId());
        result.setDetailJson(operationLog.getDetailJson());
        result.setCreatedAt(operationLog.getCreatedAt());
        return result;
    }

    private long normalizePage(Long page) {
        return page == null || page < DEFAULT_PAGE ? DEFAULT_PAGE : page;
    }

    private long normalizePageSize(Long pageSize) {
        if (pageSize == null || pageSize < DEFAULT_PAGE) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(pageSize, MAX_PAGE_SIZE);
    }
}
