package com.overduefree.module.caseitem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("success_case")
public class SuccessCase {

    public static final String STATUS_PUBLISHED = "PUBLISHED";

    @TableId(type = IdType.AUTO)
    private Long id;
    private String displayName;
    private String maskedPhone;
    private String overduePlatforms;
    private BigDecimal overdueAmount;
    private String handlingPlan;
    private String avatarUrl;
    private String detailText;
    private Integer sortOrder;
    private String status;
    private Integer deleted;
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
