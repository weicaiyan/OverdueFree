package com.overduefree.module.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.overduefree.auth.AdminPrincipal;
import com.overduefree.auth.CurrentAdminContext;
import com.overduefree.common.BusinessException;
import com.overduefree.common.ErrorCode;
import com.overduefree.module.admin.dto.AdminPasswordResetRequest;
import com.overduefree.module.admin.dto.AdminUserCreateRequest;
import com.overduefree.module.admin.dto.AdminUserResult;
import com.overduefree.module.admin.dto.AdminUserUpdateRequest;
import com.overduefree.module.admin.entity.AdminUser;
import com.overduefree.module.admin.mapper.AdminUserMapper;
import com.overduefree.module.admin.service.AdminUserManageService;
import com.overduefree.module.operationlog.entity.OperationLog;
import com.overduefree.module.operationlog.service.OperationLogService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminUserManageServiceImpl implements AdminUserManageService {

    private final AdminUserMapper adminUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final OperationLogService operationLogService;

    public AdminUserManageServiceImpl(AdminUserMapper adminUserMapper,
                                      PasswordEncoder passwordEncoder,
                                      OperationLogService operationLogService) {
        this.adminUserMapper = adminUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.operationLogService = operationLogService;
    }

    @Override
    public List<AdminUserResult> listUsers() {
        ensureBoss();
        return adminUserMapper.selectList(new LambdaQueryWrapper<AdminUser>()
                .orderByAsc(AdminUser::getId))
            .stream()
            .map(this::toResult)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdminUserResult createUser(AdminUserCreateRequest request) {
        AdminPrincipal principal = ensureBoss();
        assertUsernameAvailable(request.getUsername());

        AdminUser adminUser = new AdminUser();
        adminUser.setUsername(request.getUsername());
        adminUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        adminUser.setDisplayName(request.getDisplayName());
        adminUser.setRole(request.getRole());
        adminUser.setStatus(AdminUser.STATUS_ACTIVE);
        adminUserMapper.insert(adminUser);

        operationLogService.record(
            principal.getAdminId(),
            OperationLog.ACTION_CREATE_ADMIN,
            OperationLog.TARGET_TYPE_ADMIN_USER,
            adminUser.getId(),
            null,
            null,
            toResult(adminUser)
        );
        return toResult(adminUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdminUserResult updateUser(Long id, AdminUserUpdateRequest request) {
        AdminPrincipal principal = ensureBoss();
        AdminUser adminUser = getExistingUser(id);
        if (principal.getAdminId().equals(id) && AdminUser.STATUS_DISABLED.equals(request.getStatus())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "不能停用当前登录账号");
        }

        adminUser.setDisplayName(request.getDisplayName());
        adminUser.setRole(request.getRole());
        adminUser.setStatus(request.getStatus());
        adminUser.setUpdatedAt(LocalDateTime.now());
        adminUserMapper.updateById(adminUser);

        operationLogService.record(
            principal.getAdminId(),
            OperationLog.ACTION_UPDATE_ADMIN,
            OperationLog.TARGET_TYPE_ADMIN_USER,
            id,
            null,
            null,
            toResult(adminUser)
        );
        return toResult(adminUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long id, AdminPasswordResetRequest request) {
        AdminPrincipal principal = ensureBoss();
        AdminUser adminUser = getExistingUser(id);
        adminUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        adminUser.setUpdatedAt(LocalDateTime.now());
        adminUserMapper.updateById(adminUser);

        operationLogService.record(
            principal.getAdminId(),
            OperationLog.ACTION_RESET_ADMIN_PASSWORD,
            OperationLog.TARGET_TYPE_ADMIN_USER,
            id,
            null,
            null,
            null
        );
    }

    private AdminPrincipal ensureBoss() {
        AdminPrincipal principal = CurrentAdminContext.getRequired();
        if (!AdminUser.ROLE_BOSS.equals(principal.getRole())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "只有老板账号可以管理管理员");
        }
        return principal;
    }

    private void assertUsernameAvailable(String username) {
        Long count = adminUserMapper.selectCount(new LambdaQueryWrapper<AdminUser>()
            .eq(AdminUser::getUsername, username));
        if (count > 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "管理员账号已存在");
        }
    }

    private AdminUser getExistingUser(Long id) {
        AdminUser adminUser = adminUserMapper.selectById(id);
        if (adminUser == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "管理员不存在");
        }
        return adminUser;
    }

    private AdminUserResult toResult(AdminUser adminUser) {
        AdminUserResult result = new AdminUserResult();
        result.setId(adminUser.getId());
        result.setUsername(adminUser.getUsername());
        result.setDisplayName(adminUser.getDisplayName());
        result.setRole(adminUser.getRole());
        result.setStatus(adminUser.getStatus());
        result.setLastLoginAt(adminUser.getLastLoginAt());
        result.setCreatedAt(adminUser.getCreatedAt());
        result.setUpdatedAt(adminUser.getUpdatedAt());
        return result;
    }
}
