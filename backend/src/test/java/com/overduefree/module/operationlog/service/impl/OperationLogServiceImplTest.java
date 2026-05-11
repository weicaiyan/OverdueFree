package com.overduefree.module.operationlog.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.overduefree.auth.AdminPrincipal;
import com.overduefree.auth.CurrentAdminContext;
import com.overduefree.common.BusinessException;
import com.overduefree.common.PageResult;
import com.overduefree.module.admin.entity.AdminUser;
import com.overduefree.module.operationlog.dto.OperationLogQuery;
import com.overduefree.module.operationlog.dto.OperationLogResult;
import com.overduefree.module.operationlog.entity.OperationLog;
import com.overduefree.module.operationlog.mapper.OperationLogMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OperationLogServiceImplTest {

    private final OperationLogMapper operationLogMapper = mock(OperationLogMapper.class);
    private final OperationLogServiceImpl service = new OperationLogServiceImpl(operationLogMapper, new ObjectMapper());

    @AfterEach
    void tearDown() {
        CurrentAdminContext.clear();
    }

    @Test
    void shouldRejectNormalAdminViewingLogs() {
        CurrentAdminContext.set(new AdminPrincipal(2L, 20L, "operator", "Operator", AdminUser.ROLE_ADMIN));

        assertThatThrownBy(() -> service.listLogs(new OperationLogQuery()))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("只有老板账号");

        verify(operationLogMapper, never()).selectPage(any(), any());
    }

    @Test
    void shouldListOperationLogsForBoss() {
        CurrentAdminContext.set(new AdminPrincipal(1L, 10L, "boss", "Boss", AdminUser.ROLE_BOSS));
        OperationLog operationLog = new OperationLog();
        operationLog.setId(9L);
        operationLog.setAdminId(1L);
        operationLog.setAction(OperationLog.ACTION_EXPORT_LEADS);
        operationLog.setTargetType(OperationLog.TARGET_TYPE_LEAD);
        operationLog.setTargetId(100L);
        operationLog.setDetailJson("{\"rowCount\":1}");
        operationLog.setCreatedAt(LocalDateTime.now());
        when(operationLogMapper.selectPage(any(), any())).thenAnswer(invocation -> {
            Page<OperationLog> page = invocation.getArgument(0);
            assertThat(page.getCurrent()).isEqualTo(1L);
            assertThat(page.getSize()).isEqualTo(100L);
            page.setRecords(Collections.singletonList(operationLog));
            page.setTotal(1L);
            return page;
        });

        OperationLogQuery query = new OperationLogQuery();
        query.setPage(0L);
        query.setPageSize(200L);
        PageResult<OperationLogResult> result = service.listLogs(query);

        assertThat(result.getPage()).isEqualTo(1L);
        assertThat(result.getPageSize()).isEqualTo(100L);
        assertThat(result.getTotal()).isEqualTo(1L);
        assertThat(result.getList()).hasSize(1);
        assertThat(result.getList().get(0).getAction()).isEqualTo(OperationLog.ACTION_EXPORT_LEADS);
        verify(operationLogMapper).selectPage(any(), any());
    }
}
