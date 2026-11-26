package com.backendAPI.repository;

import com.backendAPI.models.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    List<Article> findByUserId(Long userId);

    long countByUserId(Long userId);
}
