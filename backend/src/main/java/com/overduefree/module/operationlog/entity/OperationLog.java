package com.overduefree.module.operationlog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("operation_log")
public class OperationLog {

    public static final String ACTION_EXPORT_LEADS = "EXPORT_LEADS";
    public static final String ACTION_CREATE_ADMIN = "CREATE_ADMIN";
    public static final String ACTION_UPDATE_ADMIN = "UPDATE_ADMIN";
    public static final String ACTION_RESET_ADMIN_PASSWORD = "RESET_ADMIN_PASSWORD";
    public static final String TARGET_TYPE_LEAD = "LEAD";
    public static final String TARGET_TYPE_ADMIN_USER = "ADMIN_USER";

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long adminId;
    private String action;
    private String targetType;
    private Long targetId;
    private String ip;
    private String userAgent;
    private String detailJson;
    private LocalDateTime createdAt;
}
