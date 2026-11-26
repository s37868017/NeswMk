package com.backendAPI.service;

import com.backendAPI.dto.RegisterRequest;
import com.backendAPI.dto.UserResponse;
import com.backendAPI.models.Role;
import com.backendAPI.models.User;
import com.backendAPI.repository.ArticleRepository;
import com.backendAPI.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ArticleRepository articleRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ArticleRepository articleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.articleRepository = articleRepository;
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public boolean existsByLogin(String login) {
        return userRepository.existsByLogin(login);
    }

    public UserResponse updateUser(Long id, RegisterRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setLogin(request.getLogin());
        user.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        userRepository.save(user);
        return toResponse(user);
    }

    public void updateUserRoles(Long userId, List<String> roleNames) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRoles(roleNames.stream()
                .map(Role::valueOf)
                .collect(Collectors.toSet()));
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Удаляем все статьи пользователя через JPQL
        userRepository.deleteArticlesByUserId(userId);

        // Удаляем пользователя
        userRepository.delete(user);
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return toResponse(user);
    }

    private UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setLogin(user.getLogin());
        response.setEmail(user.getEmail());
        response.setRoles(user.getRoles().stream()
                .map(Role::name)
                .collect(Collectors.toSet()));
        response.setArticlesCount(articleRepository.countByUserId(user.getId()));
        return response;
    }
}