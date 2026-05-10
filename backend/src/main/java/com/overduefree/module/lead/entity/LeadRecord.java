package com.overduefree.module.lead.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("lead_record")
public class LeadRecord {

    public static final String SOURCE_AI_CHAT = "AI_CHAT";
    public static final String SOURCE_DEBT_PLAN = "DEBT_PLAN";

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long customerId;
    private String source;
    private String surname;
    private String region;
    private BigDecimal debtAmount;
    private String debtType;
    private String debtDescription;
    private String ageRange;
    private String jobStatus;
    private String creditStatus;
    private String monthlyIncomeRange;
    private String monthlyExpenseRange;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
