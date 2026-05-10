package com.overduefree.module.customer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("mock_verification_code")
public class MockVerificationCode {

    public static final String SCENE_CUSTOMER_LOGIN = "CUSTOMER_LOGIN";

    @TableId(type = IdType.AUTO)
    private Long id;
    private String phone;
    private String code;
    private String scene;
    private LocalDateTime consumedAt;
    private LocalDateTime createdAt;

    public boolean isUnused() {
        return consumedAt == null;
    }
}
