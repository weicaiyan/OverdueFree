package com.overduefree.module.caseitem.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
public class SuccessCaseSaveRequest {

    @NotBlank
    @Size(max = 50)
    private String displayName;

    @Size(max = 30)
    private String maskedPhone;

    @Size(max = 500)
    private String overduePlatforms;

    @DecimalMin("0.00")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal overdueAmount;

    @Size(max = 500)
    private String handlingPlan;

    @Size(max = 500)
    private String avatarUrl;

    @Size(max = 10000)
    private String detailText;

    private Integer sortOrder;
}
