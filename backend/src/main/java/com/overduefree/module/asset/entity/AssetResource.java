package com.overduefree.module.asset.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("asset_resource")
public class AssetResource {

    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String KEY_HOME_VIDEO = "HOME_VIDEO";
    public static final String KEY_HOME_VIDEO_COVER = "HOME_VIDEO_COVER";
    public static final String KEY_AI_CONSULT_BANNER = "AI_CONSULT_BANNER";
    public static final String KEY_LOAN_CALCULATOR_BANNER = "LOAN_CALCULATOR_BANNER";
    public static final String KEY_DEBT_PLAN_BANNER = "DEBT_PLAN_BANNER";
    public static final String KEY_WECHAT_QR = "WECHAT_QR";

    @TableId(type = IdType.AUTO)
    private Long id;
    private String assetKey;
    private String title;
    private String fileUrl;
    private String originalFileName;
    private String mimeType;
    private Long fileSize;
    private String status;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
