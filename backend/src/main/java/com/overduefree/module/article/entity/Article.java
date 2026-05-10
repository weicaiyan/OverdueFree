package com.overduefree.module.article.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("article")
public class Article {

    public static final String STATUS_DRAFT = "DRAFT";
    public static final String STATUS_PUBLISHED = "PUBLISHED";
    public static final int DELETED_NO = 0;
    public static final int DELETED_YES = 1;

    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String coverUrl;
    private String summary;
    private String contentText;
    private LocalDateTime publishTime;
    private Integer sortOrder;
    private String status;
    private Integer deleted;
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
