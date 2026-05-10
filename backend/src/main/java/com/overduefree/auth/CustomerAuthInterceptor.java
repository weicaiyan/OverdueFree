package com.overduefree.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.overduefree.common.BusinessException;
import com.overduefree.common.ErrorCode;
import com.overduefree.module.customer.entity.Customer;
import com.overduefree.module.customer.entity.CustomerSession;
import com.overduefree.module.customer.mapper.CustomerMapper;
import com.overduefree.module.customer.mapper.CustomerSessionMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Component
public class CustomerAuthInterceptor implements HandlerInterceptor {

    private static final String OPTIONS_METHOD = "OPTIONS";

    private final AuthTokenService authTokenService;
    private final CustomerSessionMapper customerSessionMapper;
    private final CustomerMapper customerMapper;

    public CustomerAuthInterceptor(AuthTokenService authTokenService,
                                   CustomerSessionMapper customerSessionMapper,
                                   CustomerMapper customerMapper) {
        this.authTokenService = authTokenService;
        this.customerSessionMapper = customerSessionMapper;
        this.customerMapper = customerMapper;
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
        CustomerSession session = customerSessionMapper.selectOne(new LambdaQueryWrapper<CustomerSession>()
            .eq(CustomerSession::getTokenHash, tokenHash));
        LocalDateTime now = LocalDateTime.now();
        if (session == null || !session.isAvailable(now)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "登录已失效，请重新登录");
        }
        Customer customer = customerMapper.selectById(session.getCustomerId());
        if (customer == null || !customer.isActive()) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "账号不可用");
        }
        CurrentCustomerContext.set(new CustomerPrincipal(customer.getId(), session.getId()));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        CurrentCustomerContext.clear();
    }
}
