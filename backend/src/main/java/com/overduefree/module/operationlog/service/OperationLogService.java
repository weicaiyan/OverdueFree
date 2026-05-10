package com.overduefree.module.operationlog.service;

public interface OperationLogService {

    void record(Long adminId, String action, String targetType, Long targetId, String ip, String userAgent, Object detail);
}
