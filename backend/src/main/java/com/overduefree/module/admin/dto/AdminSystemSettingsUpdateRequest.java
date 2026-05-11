package com.overduefree.module.admin.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class AdminSystemSettingsUpdateRequest {

    @NotNull(message = "客户登录有效期不能为空")
    @Min(value = 1, message = "客户登录有效期不能小于 1 天")
    @Max(value = 365, message = "客户登录有效期不能大于 365 天")
    private Integer customerLoginExpireDays;
}
