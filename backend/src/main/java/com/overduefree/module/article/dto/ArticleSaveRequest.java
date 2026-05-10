package com.overduefree.module.article.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class ArticleSaveRequest {

    @NotBlank
    @Size(max = 200)
    private String title;

    @Size(max = 500)
    private String coverUrl;

    @Size(max = 500)
    private String summary;

    @Size(max = 10000)
    private String contentText;

    private Integer sortOrder;
}
