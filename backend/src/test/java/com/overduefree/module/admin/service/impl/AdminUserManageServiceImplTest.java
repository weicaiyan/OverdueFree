package com.overduefree.module.admin.service.impl;

import com.overduefree.auth.AdminPrincipal;
import com.overduefree.auth.CurrentAdminContext;
import com.overduefree.common.BusinessException;
import com.overduefree.module.admin.dto.AdminUserCreateRequest;
import com.overduefree.module.admin.dto.AdminUserUpdateRequest;
import com.overduefree.module.admin.entity.AdminUser;
import com.overduefree.module.admin.mapper.AdminUserMapper;
import com.overduefree.module.operationlog.service.OperationLogService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminUserManageServiceImplTest {

    private final AdminUserMapper adminUserMapper = mock(AdminUserMapper.class);
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final OperationLogService operationLogService = mock(OperationLogService.class);
    private final AdminUserManageServiceImpl service = new AdminUserManageServiceImpl(
        adminUserMapper,
        passwordEncoder,
        operationLogService
    );

    @AfterEach
    void tearDown() {
        CurrentAdminContext.clear();
    }

    @Test
    void shouldRejectNormalAdminManagingUsers() {
        CurrentAdminContext.set(new AdminPrincipal(2L, 20L, "operator", "Operator", AdminUser.ROLE_ADMIN));

        assertThatThrownBy(service::listUsers)
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("只有老板账号");

        verify(adminUserMapper, never()).selectList(any());
    }

    @Test
    void shouldCreateAdminWithEncodedPassword() {
        CurrentAdminContext.set(new AdminPrincipal(1L, 10L, "boss", "Boss", AdminUser.ROLE_BOSS));
        when(adminUserMapper.selectCount(any())).thenReturn(0L);
        when(adminUserMapper.insert(any(AdminUser.class))).thenAnswer(invocation -> {
            AdminUser user = invocation.getArgument(0);
            user.setId(3L);
            return 1;
        });

        AdminUserCreateRequest request = new AdminUserCreateRequest();
        request.setUsername("demo_admin");
        request.setPassword("demo123456");
        request.setDisplayName("Demo Admin");
        request.setRole(AdminUser.ROLE_ADMIN);

        service.createUser(request);

        ArgumentCaptor<AdminUser> userCaptor = ArgumentCaptor.forClass(AdminUser.class);
        verify(adminUserMapper).insert(userCaptor.capture());
        AdminUser savedUser = userCaptor.getValue();
        assertThat(savedUser.getUsername()).isEqualTo("demo_admin");
        assertThat(savedUser.getPasswordHash()).isNotEqualTo("demo123456");
        assertThat(passwordEncoder.matches("demo123456", savedUser.getPasswordHash())).isTrue();
        assertThat(savedUser.getStatus()).isEqualTo(AdminUser.STATUS_ACTIVE);
        verify(operationLogService).record(
            eq(1L),
            eq("CREATE_ADMIN"),
            eq("ADMIN_USER"),
            eq(3L),
            eq(null),
            eq(null),
            any()
        );
    }

    @Test
    void shouldRejectDisablingCurrentBossAccount() {
        CurrentAdminContext.set(new AdminPrincipal(1L, 10L, "boss", "Boss", AdminUser.ROLE_BOSS));
        AdminUser boss = new AdminUser();
        boss.setId(1L);
        boss.setUsername("boss");
        boss.setRole(AdminUser.ROLE_BOSS);
        boss.setStatus(AdminUser.STATUS_ACTIVE);
        when(adminUserMapper.selectById(1L)).thenReturn(boss);

        AdminUserUpdateRequest request = new AdminUserUpdateRequest();
        request.setDisplayName("Boss");
        request.setRole(AdminUser.ROLE_BOSS);
        request.setStatus(AdminUser.STATUS_DISABLED);

        assertThatThrownBy(() -> service.updateUser(1L, request))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("不能停用当前登录账号");

        verify(adminUserMapper, never()).updateById(any());
    }
}
