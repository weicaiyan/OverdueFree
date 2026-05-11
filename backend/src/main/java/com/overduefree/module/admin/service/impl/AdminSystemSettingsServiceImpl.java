package com.overduefree.module.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.overduefree.auth.AdminPrincipal;
import com.overduefree.auth.CurrentAdminContext;
import com.overduefree.common.BusinessException;
import com.overduefree.common.ErrorCode;
import com.overduefree.module.admin.dto.AdminSystemSettingsResult;
import com.overduefree.module.admin.dto.AdminSystemSettingsUpdateRequest;
import com.overduefree.module.admin.entity.AdminUser;
import com.overduefree.module.admin.service.AdminSystemSettingsService;
import com.overduefree.module.configitem.entity.SysConfig;
import com.overduefree.module.configitem.mapper.SysConfigMapper;
import com.overduefree.module.configitem.service.SysConfigService;
import com.overduefree.module.operationlog.entity.OperationLog;
import com.overduefree.module.operationlog.service.OperationLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AdminSystemSettingsServiceImpl implements AdminSystemSettingsService {

    private static final int DEFAULT_CUSTOMER_LOGIN_EXPIRE_DAYS = 7;
    private static final int MIN_CUSTOMER_LOGIN_EXPIRE_DAYS = 1;
    private static final int MAX_CUSTOMER_LOGIN_EXPIRE_DAYS = 365;
    private static final String CUSTOMER_LOGIN_EXPIRE_DAYS_DESCRIPTION = "客户登录有效期，单位：天";

    private final SysConfigMapper sysConfigMapper;
    private final SysConfigService sysConfigService;
    private final OperationLogService operationLogService;

    public AdminSystemSettingsServiceImpl(SysConfigMapper sysConfigMapper,
                                          SysConfigService sysConfigService,
                                          OperationLogService operationLogService) {
        this.sysConfigMapper = sysConfigMapper;
        this.sysConfigService = sysConfigService;
        this.operationLogService = operationLogService;
    }

    @Override
    public AdminSystemSettingsResult getSettings() {
        ensureBoss();
        SysConfig config = getCustomerLoginExpireConfig();
        return toResult(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdminSystemSettingsResult updateSettings(AdminSystemSettingsUpdateRequest request) {
        AdminPrincipal principal = ensureBoss();
        assertCustomerLoginExpireDaysValid(request.getCustomerLoginExpireDays());

        SysConfig config = getCustomerLoginExpireConfig();
        String oldValue = config == null ? null : config.getConfigValue();
        SysConfig savedConfig = saveCustomerLoginExpireDays(config, request.getCustomerLoginExpireDays());

        operationLogService.record(
            principal.getAdminId(),
            OperationLog.ACTION_UPDATE_SETTING,
            OperationLog.TARGET_TYPE_SYS_CONFIG,
            savedConfig.getId(),
            null,
            null,
            buildOperationDetail(oldValue, savedConfig.getConfigValue())
        );
        return toResult(savedConfig);
    }

    private AdminPrincipal ensureBoss() {
        AdminPrincipal principal = CurrentAdminContext.getRequired();
        if (!AdminUser.ROLE_BOSS.equals(principal.getRole())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "只有老板账号可以管理系统设置");
        }
        return principal;
    }

    private SysConfig getCustomerLoginExpireConfig() {
        return sysConfigMapper.selectOne(new LambdaQueryWrapper<SysConfig>()
            .eq(SysConfig::getConfigKey, SysConfig.CUSTOMER_LOGIN_EXPIRE_DAYS));
    }

    private SysConfig saveCustomerLoginExpireDays(SysConfig config, Integer expireDays) {
        LocalDateTime now = LocalDateTime.now();
        if (config == null) {
            SysConfig newConfig = new SysConfig();
            newConfig.setConfigKey(SysConfig.CUSTOMER_LOGIN_EXPIRE_DAYS);
            newConfig.setConfigValue(String.valueOf(expireDays));
            newConfig.setDescription(CUSTOMER_LOGIN_EXPIRE_DAYS_DESCRIPTION);
            newConfig.setCreatedAt(now);
            newConfig.setUpdatedAt(now);
            sysConfigMapper.insert(newConfig);
            return newConfig;
        }

        config.setConfigValue(String.valueOf(expireDays));
        config.setDescription(CUSTOMER_LOGIN_EXPIRE_DAYS_DESCRIPTION);
        config.setUpdatedAt(now);
        sysConfigMapper.updateById(config);
        return config;
    }

    private void assertCustomerLoginExpireDaysValid(Integer expireDays) {
        if (expireDays == null
            || expireDays < MIN_CUSTOMER_LOGIN_EXPIRE_DAYS
            || expireDays > MAX_CUSTOMER_LOGIN_EXPIRE_DAYS) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "客户登录有效期必须在 1 到 365 天之间");
        }
    }

    private Map<String, String> buildOperationDetail(String oldValue, String newValue) {
        Map<String, String> detailMap = new LinkedHashMap<>();
        detailMap.put("configKey", SysConfig.CUSTOMER_LOGIN_EXPIRE_DAYS);
        detailMap.put("oldValue", oldValue);
        detailMap.put("newValue", newValue);
        return detailMap;
    }

    private AdminSystemSettingsResult toResult(SysConfig config) {
        AdminSystemSettingsResult result = new AdminSystemSettingsResult();
        int expireDays = sysConfigService.getIntValue(
            SysConfig.CUSTOMER_LOGIN_EXPIRE_DAYS,
            DEFAULT_CUSTOMER_LOGIN_EXPIRE_DAYS
        );
        result.setCustomerLoginExpireDays(Math.max(expireDays, MIN_CUSTOMER_LOGIN_EXPIRE_DAYS));
        if (config != null) {
            result.setUpdatedAt(config.getUpdatedAt());
        }
        return result;
    }
}
