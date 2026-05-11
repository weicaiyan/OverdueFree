package com.overduefree.module.operationlog.service;

import com.overduefree.common.PageResult;
import com.overduefree.module.operationlog.dto.OperationLogQuery;
import com.overduefree.module.operationlog.dto.OperationLogResult;

public interface OperationLogService {

    void record(Long adminId, String action, String targetType, Long targetId, String ip, String userAgent, Object detail);

    PageResult<OperationLogResult> listLogs(OperationLogQuery query);
}
