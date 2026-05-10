package com.overduefree.module.lead.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("customer_event")
public class CustomerEvent {

    public static final String EVENT_VIEW_WECHAT_QR = "VIEW_WECHAT_QR";

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long customerId;
    private String eventType;
    private String sourcePage;
    private String refType;
    private Long refId;
    private String metadataJson;
    private LocalDateTime createdAt;
}
