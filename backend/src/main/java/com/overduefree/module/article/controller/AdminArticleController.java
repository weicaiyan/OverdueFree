package com.overduefree.module.article.controller;

import com.overduefree.common.ApiResponse;
import com.overduefree.common.PageResult;
import com.overduefree.module.article.dto.ArticleQuery;
import com.overduefree.module.article.dto.ArticleResult;
import com.overduefree.module.article.dto.ArticleSaveRequest;
import com.overduefree.module.article.service.ArticleService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/admin/articles")
public class AdminArticleController {

    private final ArticleService articleService;

    public AdminArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public ApiResponse<PageResult<ArticleResult>> listArticles(@ModelAttribute ArticleQuery query) {
        return ApiResponse.ok(articleService.listAdmin(query));
    }

    @PostMapping
    public ApiResponse<ArticleResult> createArticle(@Valid @RequestBody ArticleSaveRequest request) {
        return ApiResponse.ok(articleService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<ArticleResult> updateArticle(@PathVariable Long id,
                                                    @Valid @RequestBody ArticleSaveRequest request) {
        return ApiResponse.ok(articleService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteArticle(@PathVariable Long id) {
        articleService.delete(id);
        return ApiResponse.ok();
    }

    @PatchMapping("/{id}/publish")
    public ApiResponse<ArticleResult> publishArticle(@PathVariable Long id) {
        return ApiResponse.ok(articleService.publish(id));
    }

    @PatchMapping("/{id}/offline")
    public ApiResponse<ArticleResult> offlineArticle(@PathVariable Long id) {
        return ApiResponse.ok(articleService.offline(id));
    }
}
