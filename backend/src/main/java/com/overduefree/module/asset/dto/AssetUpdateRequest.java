package com.overduefree.module.asset.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class AssetUpdateRequest {

    @NotBlank
    @Size(max = 100)
    private String title;

    @Size(max = 500)
    private String fileUrl;

    @Size(max = 255)
    private String originalFileName;

    @Size(max = 100)
    private String mimeType;

    private Long fileSize;
}
