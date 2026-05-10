package com.overduefree.module.article.service;

import com.overduefree.common.PageResult;
import com.overduefree.module.article.dto.ArticleQuery;
import com.overduefree.module.article.dto.ArticleResult;
import com.overduefree.module.article.dto.ArticleSaveRequest;

public interface ArticleService {

    PageResult<ArticleResult> listPublished(Long page, Long pageSize);

    ArticleResult getPublished(Long id);

    PageResult<ArticleResult> listAdmin(ArticleQuery query);

    ArticleResult create(ArticleSaveRequest request);

    ArticleResult update(Long id, ArticleSaveRequest request);

    void delete(Long id);

    ArticleResult publish(Long id);

    ArticleResult offline(Long id);
}
