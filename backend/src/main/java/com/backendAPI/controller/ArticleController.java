package com.backendAPI.controller;

import com.backendAPI.dto.ArticleRequest;
import com.backendAPI.dto.ArticleResponse;
import com.backendAPI.dto.UserResponse;
import com.backendAPI.service.ArticleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {
    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'REDACTOR', 'ADMIN')")
    public ResponseEntity<ArticleResponse> createArticle(@RequestBody ArticleRequest request, Authentication authentication) {
        return ResponseEntity.ok(articleService.createArticle(request, authentication));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponse> getArticle(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.getArticle(id));
    }

    @GetMapping
    public ResponseEntity<List<ArticleResponse>> getAll() {
        return ResponseEntity.ok(articleService.getAllArticles());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'REDACTOR', 'ADMIN')")
    public ResponseEntity<ArticleResponse> updateArticle(@PathVariable Long id, @RequestBody ArticleRequest request, Authentication authentication) {
        return ResponseEntity.ok(articleService.updateArticle(id, request, authentication));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'REDACTOR', 'ADMIN')")
    public ResponseEntity<?> deleteArticle(@PathVariable Long id, Authentication authentication) {
        articleService.deleteArticle(id, authentication);
        return ResponseEntity.ok().build();
    }
}