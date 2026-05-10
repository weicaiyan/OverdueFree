package com.overduefree.module.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.overduefree.auth.AdminPrincipal;
import com.overduefree.auth.AuthTokenService;
import com.overduefree.auth.CurrentAdminContext;
import com.overduefree.common.BusinessException;
import com.overduefree.common.ErrorCode;
import com.overduefree.config.AuthProperties;
import com.overduefree.module.admin.dto.AdminLoginRequest;
import com.overduefree.module.admin.dto.AdminLoginResult;
import com.overduefree.module.admin.dto.AdminMeResult;
import com.overduefree.module.admin.entity.AdminSession;
import com.overduefree.module.admin.entity.AdminUser;
import com.overduefree.module.admin.mapper.AdminSessionMapper;
import com.overduefree.module.admin.mapper.AdminUserMapper;
import com.overduefree.module.admin.service.AdminAuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AdminAuthServiceImpl implements AdminAuthService {

    private final AdminUserMapper adminUserMapper;
    private final AdminSessionMapper adminSessionMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenService authTokenService;
    private final AuthProperties authProperties;

    public AdminAuthServiceImpl(AdminUserMapper adminUserMapper,
                                AdminSessionMapper adminSessionMapper,
                                PasswordEncoder passwordEncoder,
                                AuthTokenService authTokenService,
                                AuthProperties authProperties) {
        this.adminUserMapper = adminUserMapper;
        this.adminSessionMapper = adminSessionMapper;
        this.passwordEncoder = passwordEncoder;
        this.authTokenService = authTokenService;
        this.authProperties = authProperties;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdminLoginResult login(AdminLoginRequest request) {
        AdminUser adminUser = adminUserMapper.selectOne(new LambdaQueryWrapper<AdminUser>()
            .eq(AdminUser::getUsername, request.getUsername()));
        if (adminUser == null || !passwordEncoder.matches(request.getPassword(), adminUser.getPasswordHash())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "账号或密码错误");
        }
        if (!adminUser.isActive()) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "管理员账号已停用");
        }
        LocalDateTime now = LocalDateTime.now();
        adminUser.setLastLoginAt(now);
        adminUserMapper.updateById(adminUser);

        String token = authTokenService.generateToken();
        LocalDateTime expiresAt = now.plusHours(authProperties.getAdminTokenHours());
        AdminSession session = new AdminSession();
        session.setAdminId(adminUser.getId());
        session.setTokenHash(authTokenService.hashToken(token));
        session.setExpiresAt(expiresAt);
        adminSessionMapper.insert(session);

        AdminLoginResult result = new AdminLoginResult();
        result.setToken(token);
        result.setRole(adminUser.getRole());
        result.setDisplayName(adminUser.getDisplayName());
        result.setExpiresAt(expiresAt);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logout() {
        AdminPrincipal principal = CurrentAdminContext.getRequired();
        AdminSession session = new AdminSession();
        session.setId(principal.getSessionId());
        session.setRevokedAt(LocalDateTime.now());
        adminSessionMapper.updateById(session);
    }

    @Override
    public AdminMeResult me() {
        AdminPrincipal principal = CurrentAdminContext.getRequired();
        AdminMeResult result = new AdminMeResult();
        result.setAdminId(principal.getAdminId());
        result.setUsername(principal.getUsername());
        result.setDisplayName(principal.getDisplayName());
        result.setRole(principal.getRole());
        return result;
    }
}
