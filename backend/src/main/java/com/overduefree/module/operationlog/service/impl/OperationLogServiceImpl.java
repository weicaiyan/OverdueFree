package com.overduefree.module.operationlog.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.overduefree.module.operationlog.entity.OperationLog;
import com.overduefree.module.operationlog.mapper.OperationLogMapper;
import com.overduefree.module.operationlog.service.OperationLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OperationLogServiceImpl implements OperationLogService {

    private static final Logger log = LoggerFactory.getLogger(OperationLogServiceImpl.class);

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
}
