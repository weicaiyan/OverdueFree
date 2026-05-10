package com.overduefree.module.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("admin_session")
public class AdminSession {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long adminId;
    private String tokenHash;
    private LocalDateTime expiresAt;
    private LocalDateTime revokedAt;
    private LocalDateTime createdAt;

    public boolean isAvailable(LocalDateTime now) {
        return revokedAt == null && expiresAt != null && expiresAt.isAfter(now);
    }
}
