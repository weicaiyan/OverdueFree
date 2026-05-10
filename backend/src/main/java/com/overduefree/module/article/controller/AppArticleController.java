package com.overduefree.module.article.controller;

import com.overduefree.common.ApiResponse;
import com.overduefree.common.PageResult;
import com.overduefree.module.article.dto.ArticleResult;
import com.overduefree.module.article.service.ArticleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app/articles")
public class AppArticleController {

    private final ArticleService articleService;

    public AppArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public ApiResponse<PageResult<ArticleResult>> listPublished(@RequestParam(defaultValue = "1") Long page,
                                                                @RequestParam(defaultValue = "10") Long pageSize) {
        return ApiResponse.ok(articleService.listPublished(page, pageSize));
    }

    @GetMapping("/{id}")
    public ApiResponse<ArticleResult> getPublished(@PathVariable Long id) {
        return ApiResponse.ok(articleService.getPublished(id));
    }
}
