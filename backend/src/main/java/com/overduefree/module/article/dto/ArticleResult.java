package com.overduefree.module.article.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArticleResult {

    private Long id;
    private String title;
    private String coverUrl;
    private String summary;
    private String contentText;
    private LocalDateTime publishTime;
    private Integer sortOrder;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
