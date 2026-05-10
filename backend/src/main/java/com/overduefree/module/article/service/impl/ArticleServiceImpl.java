package com.overduefree.module.article.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.overduefree.auth.CurrentAdminContext;
import com.overduefree.common.BusinessException;
import com.overduefree.common.ErrorCode;
import com.overduefree.common.PageResult;
import com.overduefree.module.article.dto.ArticleQuery;
import com.overduefree.module.article.dto.ArticleResult;
import com.overduefree.module.article.dto.ArticleSaveRequest;
import com.overduefree.module.article.entity.Article;
import com.overduefree.module.article.mapper.ArticleMapper;
import com.overduefree.module.article.service.ArticleService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl implements ArticleService {

    private static final long DEFAULT_PAGE = 1L;
    private static final long DEFAULT_PAGE_SIZE = 10L;
    private static final long MAX_PAGE_SIZE = 100L;

    private final ArticleMapper articleMapper;

    public ArticleServiceImpl(ArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
    }

    @Override
    public PageResult<ArticleResult> listPublished(Long page, Long pageSize) {
        Page<Article> pageRequest = new Page<>(normalizePage(page), normalizePageSize(pageSize));
        Page<Article> resultPage = articleMapper.selectPage(pageRequest, new LambdaQueryWrapper<Article>()
            .eq(Article::getStatus, Article.STATUS_PUBLISHED)
            .eq(Article::getDeleted, Article.DELETED_NO)
            .orderByDesc(Article::getSortOrder)
            .orderByDesc(Article::getPublishTime)
            .orderByDesc(Article::getId));
        return toPageResult(resultPage);
    }

    @Override
    public ArticleResult getPublished(Long id) {
        Article article = articleMapper.selectOne(new LambdaQueryWrapper<Article>()
            .eq(Article::getId, id)
            .eq(Article::getStatus, Article.STATUS_PUBLISHED)
            .eq(Article::getDeleted, Article.DELETED_NO));
        if (article == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "资讯不存在");
        }
        return toResult(article);
    }

    @Override
    public PageResult<ArticleResult> listAdmin(ArticleQuery query) {
        ArticleQuery normalizedQuery = query == null ? new ArticleQuery() : query;
        Page<Article> pageRequest = new Page<>(
            normalizePage(normalizedQuery.getPage()),
            normalizePageSize(normalizedQuery.getPageSize())
        );
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>()
            .eq(Article::getDeleted, Article.DELETED_NO)
            .like(StringUtils.hasText(normalizedQuery.getKeyword()), Article::getTitle, normalizedQuery.getKeyword())
            .eq(StringUtils.hasText(normalizedQuery.getStatus()), Article::getStatus, normalizedQuery.getStatus())
            .orderByDesc(Article::getSortOrder)
            .orderByDesc(Article::getUpdatedAt)
            .orderByDesc(Article::getId);
        return toPageResult(articleMapper.selectPage(pageRequest, wrapper));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArticleResult create(ArticleSaveRequest request) {
        Long adminId = CurrentAdminContext.getRequired().getAdminId();
        Article article = new Article();
        fillArticle(article, request);
        article.setStatus(Article.STATUS_DRAFT);
        article.setDeleted(Article.DELETED_NO);
        article.setCreatedBy(adminId);
        article.setUpdatedBy(adminId);
        articleMapper.insert(article);
        return toResult(article);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArticleResult update(Long id, ArticleSaveRequest request) {
        Article article = getAdminArticle(id);
        fillArticle(article, request);
        article.setUpdatedBy(CurrentAdminContext.getRequired().getAdminId());
        articleMapper.updateById(article);
        return toResult(article);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Article article = getAdminArticle(id);
        article.setDeleted(Article.DELETED_YES);
        article.setUpdatedBy(CurrentAdminContext.getRequired().getAdminId());
        articleMapper.updateById(article);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArticleResult publish(Long id) {
        Article article = getAdminArticle(id);
        article.setStatus(Article.STATUS_PUBLISHED);
        article.setPublishTime(LocalDateTime.now());
        article.setUpdatedBy(CurrentAdminContext.getRequired().getAdminId());
        articleMapper.updateById(article);
        return toResult(article);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArticleResult offline(Long id) {
        Article article = getAdminArticle(id);
        article.setStatus(Article.STATUS_DRAFT);
        article.setUpdatedBy(CurrentAdminContext.getRequired().getAdminId());
        articleMapper.updateById(article);
        return toResult(article);
    }

    private Article getAdminArticle(Long id) {
        Article article = articleMapper.selectOne(new LambdaQueryWrapper<Article>()
            .eq(Article::getId, id)
            .eq(Article::getDeleted, Article.DELETED_NO));
        if (article == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "资讯不存在");
        }
        return article;
    }

    private void fillArticle(Article article, ArticleSaveRequest request) {
        article.setTitle(request.getTitle());
        article.setCoverUrl(request.getCoverUrl());
        article.setSummary(request.getSummary());
        article.setContentText(request.getContentText());
        article.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
    }

    private PageResult<ArticleResult> toPageResult(Page<Article> page) {
        return new PageResult<>(
            page.getRecords().stream().map(this::toResult).collect(Collectors.toList()),
            page.getCurrent(),
            page.getSize(),
            page.getTotal()
        );
    }

    private ArticleResult toResult(Article article) {
        ArticleResult result = new ArticleResult();
        BeanUtils.copyProperties(article, result);
        return result;
    }

    private long normalizePage(Long page) {
        return page == null || page < DEFAULT_PAGE ? DEFAULT_PAGE : page;
    }

    private long normalizePageSize(Long pageSize) {
        if (pageSize == null || pageSize < DEFAULT_PAGE) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(pageSize, MAX_PAGE_SIZE);
    }
}
