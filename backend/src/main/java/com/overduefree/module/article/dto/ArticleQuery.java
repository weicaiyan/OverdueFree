package com.overduefree.module.article.dto;

import lombok.Data;

@Data
public class ArticleQuery {

    private String keyword;
    private String status;
    private Long page = 1L;
    private Long pageSize = 10L;
}
