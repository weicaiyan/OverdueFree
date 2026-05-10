package com.overduefree.module.asset.dto;

import lombok.Data;

@Data
public class FileUploadResult {

    private String url;
    private String originalFileName;
    private String mimeType;
    private Long fileSize;
}
