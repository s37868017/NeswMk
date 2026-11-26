package com.backendAPI.service;

import com.backendAPI.dto.ArticleRequest;
import com.backendAPI.dto.ArticleResponse;
//import com.backendAPI.dto.UserResponse;
import com.backendAPI.models.Article;
import com.backendAPI.models.User;
import com.backendAPI.repository.ArticleRepository;
import com.backendAPI.repository.UserRepository;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    public ArticleService(ArticleRepository articleRepository, UserRepository userRepository) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
    }

    public ArticleResponse createArticle(ArticleRequest request, Authentication authentication) {
        User user = userRepository.findByLogin(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setIntro(request.getIntro());
        article.setText(request.getText());
        article.setUser(user);
        return toResponse(articleRepository.save(article));
    }

    public ArticleResponse getArticle(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));
        return toResponse(article);
    }

    public List<ArticleResponse> getAllArticles() {
        return articleRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ArticleResponse updateArticle(Long id, ArticleRequest request, Authentication authentication) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        boolean isAuthor = article.getUser().getLogin().equals(authentication.getName());
        boolean isAdminOrRedactor = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_REDACTOR"));

        if (!isAuthor && !isAdminOrRedactor) {
            throw new RuntimeException("Unauthorized");
        }

        article.setTitle(request.getTitle());
        article.setIntro(request.getIntro());
        article.setText(request.getText());
        return toResponse(articleRepository.save(article));
    }

    public void deleteArticle(Long id, Authentication authentication) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        boolean isAuthor = article.getUser().getLogin().equals(authentication.getName());
        boolean isAdminOrRedactor = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_REDACTOR"));

        if (!isAuthor && !isAdminOrRedactor) {
            throw new RuntimeException("Unauthorized");
        }
        articleRepository.deleteById(id);
    }

    private ArticleResponse toResponse(Article article) {
        ArticleResponse response = new ArticleResponse();
        response.setId(article.getId());
        response.setTitle(article.getTitle());
        response.setIntro(article.getIntro());
        response.setText(article.getText());
        response.setCreatedAt(article.getCreatedAt());

        // Получаем данные пользователя
        User user = article.getUser();
        if (user != null) {
            response.setUserId(user.getId());
            response.setUserName(user.getLogin());
        }

        return response;
    }
}