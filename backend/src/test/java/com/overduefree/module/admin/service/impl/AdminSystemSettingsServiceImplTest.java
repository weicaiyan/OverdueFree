package com.overduefree.module.admin.service.impl;

import com.overduefree.auth.AdminPrincipal;
import com.overduefree.auth.CurrentAdminContext;
import com.overduefree.common.BusinessException;
import com.overduefree.module.admin.dto.AdminSystemSettingsResult;
import com.overduefree.module.admin.dto.AdminSystemSettingsUpdateRequest;
import com.overduefree.module.admin.entity.AdminUser;
import com.overduefree.module.configitem.entity.SysConfig;
import com.overduefree.module.configitem.mapper.SysConfigMapper;
import com.overduefree.module.configitem.service.SysConfigService;
import com.overduefree.module.operationlog.entity.OperationLog;
import com.overduefree.module.operationlog.service.OperationLogService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminSystemSettingsServiceImplTest {

    private final SysConfigMapper sysConfigMapper = mock(SysConfigMapper.class);
    private final SysConfigService sysConfigService = mock(SysConfigService.class);
    private final OperationLogService operationLogService = mock(OperationLogService.class);
    private final AdminSystemSettingsServiceImpl service = new AdminSystemSettingsServiceImpl(
        sysConfigMapper,
        sysConfigService,
        operationLogService
    );

    @AfterEach
    void tearDown() {
        CurrentAdminContext.clear();
    }

    @Test
    void shouldRejectNormalAdminUpdatingSettings() {
        CurrentAdminContext.set(new AdminPrincipal(2L, 20L, "operator", "Operator", AdminUser.ROLE_ADMIN));
        AdminSystemSettingsUpdateRequest request = new AdminSystemSettingsUpdateRequest();
        request.setCustomerLoginExpireDays(7);

        assertThatThrownBy(() -> service.updateSettings(request))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("只有老板账号");

        verify(sysConfigMapper, never()).updateById(any());
    }

    @Test
    void shouldReadCustomerLoginExpireDaysFromConfigService() {
        CurrentAdminContext.set(new AdminPrincipal(1L, 10L, "boss", "Boss", AdminUser.ROLE_BOSS));
        when(sysConfigService.getIntValue(SysConfig.CUSTOMER_LOGIN_EXPIRE_DAYS, 7)).thenReturn(14);

        AdminSystemSettingsResult result = service.getSettings();

        assertThat(result.getCustomerLoginExpireDays()).isEqualTo(14);
    }

    @Test
    void shouldUpdateExistingCustomerLoginExpireDays() {
        CurrentAdminContext.set(new AdminPrincipal(1L, 10L, "boss", "Boss", AdminUser.ROLE_BOSS));
        SysConfig config = new SysConfig();
        config.setId(8L);
        config.setConfigKey(SysConfig.CUSTOMER_LOGIN_EXPIRE_DAYS);
        config.setConfigValue("7");
        when(sysConfigMapper.selectOne(any())).thenReturn(config);
        when(sysConfigService.getIntValue(SysConfig.CUSTOMER_LOGIN_EXPIRE_DAYS, 7)).thenReturn(30);

        AdminSystemSettingsUpdateRequest request = new AdminSystemSettingsUpdateRequest();
        request.setCustomerLoginExpireDays(30);
        AdminSystemSettingsResult result = service.updateSettings(request);

        assertThat(result.getCustomerLoginExpireDays()).isEqualTo(30);
        ArgumentCaptor<SysConfig> configCaptor = ArgumentCaptor.forClass(SysConfig.class);
        verify(sysConfigMapper).updateById(configCaptor.capture());
        assertThat(configCaptor.getValue().getConfigValue()).isEqualTo("30");
        verify(operationLogService).record(
            eq(1L),
            eq(OperationLog.ACTION_UPDATE_SETTING),
            eq(OperationLog.TARGET_TYPE_SYS_CONFIG),
            eq(8L),
            eq(null),
            eq(null),
            any()
        );
    }

    @Test
    void shouldRejectOutOfRangeCustomerLoginExpireDays() {
        CurrentAdminContext.set(new AdminPrincipal(1L, 10L, "boss", "Boss", AdminUser.ROLE_BOSS));
        AdminSystemSettingsUpdateRequest request = new AdminSystemSettingsUpdateRequest();
        request.setCustomerLoginExpireDays(366);

        assertThatThrownBy(() -> service.updateSettings(request))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("1 到 365");

        verify(sysConfigMapper, never()).updateById(any());
        verify(sysConfigMapper, never()).insert(any());
    }
}
