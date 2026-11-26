package com.backendAPI.repository;

import com.backendAPI.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLogin(String login);

    boolean existsByLogin(String login);

    // Метод для удаления всех статей пользователя через JPQL
    @Modifying
    @Query("DELETE FROM Article a WHERE a.user.id = :userId")
    void deleteArticlesByUserId(@Param("userId") Long userId);
}