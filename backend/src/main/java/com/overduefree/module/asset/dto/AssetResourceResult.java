package com.overduefree.module.asset.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssetResourceResult {

    private Long id;
    private String assetKey;
    private String title;
    private String fileUrl;
    private String originalFileName;
    private String mimeType;
    private Long fileSize;
    private String status;
    private LocalDateTime updatedAt;
}
