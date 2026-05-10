package com.overduefree.module.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.overduefree.auth.AuthTokenService;
import com.overduefree.auth.CurrentCustomerContext;
import com.overduefree.auth.CustomerPrincipal;
import com.overduefree.common.BusinessException;
import com.overduefree.common.ErrorCode;
import com.overduefree.config.AuthProperties;
import com.overduefree.module.configitem.entity.SysConfig;
import com.overduefree.module.configitem.service.SysConfigService;
import com.overduefree.module.customer.dto.CustomerLoginRequest;
import com.overduefree.module.customer.dto.CustomerLoginResult;
import com.overduefree.module.customer.dto.CustomerMeResult;
import com.overduefree.module.customer.dto.SendCodeRequest;
import com.overduefree.module.customer.dto.SendCodeResult;
import com.overduefree.module.customer.entity.Customer;
import com.overduefree.module.customer.entity.CustomerSession;
import com.overduefree.module.customer.entity.MockVerificationCode;
import com.overduefree.module.customer.mapper.CustomerMapper;
import com.overduefree.module.customer.mapper.CustomerSessionMapper;
import com.overduefree.module.customer.mapper.MockVerificationCodeMapper;
import com.overduefree.module.customer.service.CustomerAuthService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class CustomerAuthServiceImpl implements CustomerAuthService {

    private static final int MIN_CODE = 100000;
    private static final int MAX_CODE_EXCLUSIVE = 1000000;
    private static final int MIN_TOKEN_DAYS = 1;

    private final MockVerificationCodeMapper mockVerificationCodeMapper;
    private final CustomerMapper customerMapper;
    private final CustomerSessionMapper customerSessionMapper;
    private final AuthTokenService authTokenService;
    private final SysConfigService sysConfigService;
    private final AuthProperties authProperties;

    public CustomerAuthServiceImpl(MockVerificationCodeMapper mockVerificationCodeMapper,
                                   CustomerMapper customerMapper,
                                   CustomerSessionMapper customerSessionMapper,
                                   AuthTokenService authTokenService,
                                   SysConfigService sysConfigService,
                                   AuthProperties authProperties) {
        this.mockVerificationCodeMapper = mockVerificationCodeMapper;
        this.customerMapper = customerMapper;
        this.customerSessionMapper = customerSessionMapper;
        this.authTokenService = authTokenService;
        this.sysConfigService = sysConfigService;
        this.authProperties = authProperties;
    }

    @Override
    public SendCodeResult sendCode(SendCodeRequest request) {
        String code = String.valueOf(ThreadLocalRandom.current().nextInt(MIN_CODE, MAX_CODE_EXCLUSIVE));
        MockVerificationCode verificationCode = new MockVerificationCode();
        verificationCode.setPhone(request.getPhone());
        verificationCode.setCode(code);
        verificationCode.setScene(MockVerificationCode.SCENE_CUSTOMER_LOGIN);
        mockVerificationCodeMapper.insert(verificationCode);

        SendCodeResult result = new SendCodeResult();
        result.setMockCode(code);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerLoginResult login(CustomerLoginRequest request) {
        MockVerificationCode latestCode = mockVerificationCodeMapper.selectOne(
            new LambdaQueryWrapper<MockVerificationCode>()
                .eq(MockVerificationCode::getPhone, request.getPhone())
                .eq(MockVerificationCode::getScene, MockVerificationCode.SCENE_CUSTOMER_LOGIN)
                .isNull(MockVerificationCode::getConsumedAt)
                .orderByDesc(MockVerificationCode::getCreatedAt)
                .last("LIMIT 1")
        );
        if (latestCode == null || !latestCode.isUnused() || !request.getCode().equals(latestCode.getCode())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "验证码不正确");
        }

        LocalDateTime now = LocalDateTime.now();
        latestCode.setConsumedAt(now);
        mockVerificationCodeMapper.updateById(latestCode);

        Customer customer = getOrCreateCustomer(request.getPhone(), now);
        String token = authTokenService.generateToken();
        LocalDateTime expiresAt = now.plusDays(resolveCustomerTokenDays());
        CustomerSession session = new CustomerSession();
        session.setCustomerId(customer.getId());
        session.setTokenHash(authTokenService.hashToken(token));
        session.setExpiresAt(expiresAt);
        customerSessionMapper.insert(session);

        CustomerLoginResult result = new CustomerLoginResult();
        result.setToken(token);
        result.setExpiresAt(expiresAt);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logout() {
        CustomerPrincipal principal = CurrentCustomerContext.getRequired();
        CustomerSession session = new CustomerSession();
        session.setId(principal.getSessionId());
        session.setRevokedAt(LocalDateTime.now());
        customerSessionMapper.updateById(session);
    }

    @Override
    public CustomerMeResult me() {
        CurrentCustomerContext.getRequired();
        CustomerMeResult result = new CustomerMeResult();
        result.setLoggedIn(true);
        return result;
    }

    private Customer getOrCreateCustomer(String phone, LocalDateTime now) {
        Customer customer = customerMapper.selectOne(new LambdaQueryWrapper<Customer>()
            .eq(Customer::getPhone, phone));
        if (customer == null) {
            Customer newCustomer = new Customer();
            newCustomer.setPhone(phone);
            newCustomer.setStatus(Customer.STATUS_ACTIVE);
            newCustomer.setFirstLoginAt(now);
            newCustomer.setLastLoginAt(now);
            customerMapper.insert(newCustomer);
            return newCustomer;
        }
        if (!customer.isActive()) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "账号不可用");
        }
        customer.setLastLoginAt(now);
        customerMapper.updateById(customer);
        return customer;
    }

    private int resolveCustomerTokenDays() {
        int days = sysConfigService.getIntValue(
            SysConfig.CUSTOMER_LOGIN_EXPIRE_DAYS,
            authProperties.getCustomerTokenDays()
        );
        return Math.max(days, MIN_TOKEN_DAYS);
    }
}
