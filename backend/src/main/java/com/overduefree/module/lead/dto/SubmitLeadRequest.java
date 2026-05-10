package com.overduefree.module.lead.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
public class SubmitLeadRequest {

    @NotBlank
    @Size(max = 30)
    private String source;

    @NotBlank
    @Size(max = 50)
    private String surname;

    @NotBlank
    @Size(max = 100)
    private String region;

    @NotNull
    @DecimalMin("0.01")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal debtAmount;

    @NotBlank
    @Size(max = 30)
    private String debtType;

    @Size(max = 2000)
    private String debtDescription;

    @Size(max = 30)
    private String ageRange;

    @Size(max = 50)
    private String jobStatus;

    @Size(max = 50)
    private String creditStatus;

    @Size(max = 50)
    private String monthlyIncomeRange;

    @Size(max = 50)
    private String monthlyExpenseRange;
}
