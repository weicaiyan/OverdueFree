package com.overduefree.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.overduefree.common.BusinessException;
import com.overduefree.common.ErrorCode;
import com.overduefree.module.admin.entity.AdminSession;
import com.overduefree.module.admin.entity.AdminUser;
import com.overduefree.module.admin.mapper.AdminSessionMapper;
import com.overduefree.module.admin.mapper.AdminUserMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    private static final String OPTIONS_METHOD = "OPTIONS";

    private final AuthTokenService authTokenService;
    private final AdminSessionMapper adminSessionMapper;
    private final AdminUserMapper adminUserMapper;

    public AdminAuthInterceptor(AuthTokenService authTokenService,
                                AdminSessionMapper adminSessionMapper,
                                AdminUserMapper adminUserMapper) {
        this.authTokenService = authTokenService;
        this.adminSessionMapper = adminSessionMapper;
        this.adminUserMapper = adminUserMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (OPTIONS_METHOD.equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String token = authTokenService.resolveBearerToken(request.getHeader(AuthTokenService.AUTHORIZATION_HEADER));
        if (!StringUtils.hasText(token)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        }
        String tokenHash = authTokenService.hashToken(token);
        AdminSession session = adminSessionMapper.selectOne(new LambdaQueryWrapper<AdminSession>()
            .eq(AdminSession::getTokenHash, tokenHash));
        LocalDateTime now = LocalDateTime.now();
        if (session == null || !session.isAvailable(now)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "登录已失效，请重新登录");
        }
        AdminUser adminUser = adminUserMapper.selectById(session.getAdminId());
        if (adminUser == null || !adminUser.isActive()) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "管理员账号不可用");
        }
        CurrentAdminContext.set(new AdminPrincipal(
            adminUser.getId(),
            session.getId(),
            adminUser.getUsername(),
            adminUser.getDisplayName(),
            adminUser.getRole()
        ));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        CurrentAdminContext.clear();
    }
}
