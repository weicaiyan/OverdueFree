package com.overduefree.module.lead.service.impl;

import com.overduefree.auth.AdminPrincipal;
import com.overduefree.auth.CurrentAdminContext;
import com.overduefree.module.admin.entity.AdminUser;
import com.overduefree.module.lead.dto.AdminLeadQuery;
import com.overduefree.module.lead.dto.LeadExportRequest;
import com.overduefree.module.lead.service.AdminLeadService;
import com.overduefree.module.operationlog.service.OperationLogService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LeadExportServiceImplTest {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final AdminLeadService adminLeadService = mock(AdminLeadService.class);
    private final OperationLogService operationLogService = mock(OperationLogService.class);
    private final LeadExportServiceImpl service = new LeadExportServiceImpl(adminLeadService, operationLogService);

    @AfterEach
    void tearDown() {
        CurrentAdminContext.clear();
    }

    @Test
    void shouldWriteCompatibleContentDispositionForExcelExport() {
        CurrentAdminContext.set(new AdminPrincipal(1L, 10L, "boss", "Boss", AdminUser.ROLE_BOSS));
        when(adminLeadService.listLeadsForExport(any(AdminLeadQuery.class))).thenReturn(Collections.emptyList());
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        service.exportLeads(new LeadExportRequest(), request, response);

        String today = LocalDate.now().format(DATE_FORMATTER);
        String disposition = response.getHeader("Content-Disposition");
        assertThat(disposition).contains("attachment");
        assertThat(disposition).contains("filename=\"leads-export-" + today + ".xlsx\"");
        assertThat(disposition).contains("filename*=UTF-8''");
        assertThat(response.getContentType())
            .contains("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        verify(operationLogService).record(any(), any(), any(), any(), any(), any(), any());
    }
}
