package com.backendAPI.controller;

import com.backendAPI.dto.RegisterRequest;
import com.backendAPI.dto.UserResponse;
import com.backendAPI.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/exists/{login}")
    public ResponseEntity<Boolean> isUserExists(@PathVariable String login) {
        try {
            return ResponseEntity.ok(userService.existsByLogin(login));
        } catch (RuntimeException e) {
            // Возвращаем false вместо строки, чтобы соответствовать типу ResponseEntity<Boolean>
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        try {
            return ResponseEntity.ok(userService.getAllUsers());
        } catch (RuntimeException e) {
            // Возвращаем пустой список вместо строки, чтобы соответствовать типу ResponseEntity<List<UserResponse>>
            return new ResponseEntity<>(List.of(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{userId}/roles")
    public ResponseEntity<?> updateUserRoles(@PathVariable Long userId, @RequestBody List<String> roleNames) {
        try {
            userService.updateUserRoles(userId, roleNames);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok("User deleted successfully");
        } catch (RuntimeException e) {
            String message = e.getMessage();
            if (message != null && message.contains("User not found")) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
            // Обработка constraint violation (если cascade не сработал)
            if (message != null && message.contains("constraint")) {
                return new ResponseEntity<>("Cannot delete user: associated articles exist", HttpStatus.CONFLICT);  // 409 Conflict
            }
            return new ResponseEntity<>("Error deleting user: " + (message != null ? message : "Unknown error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody RegisterRequest request) {
        try {
            return ResponseEntity.ok(userService.updateUser(id, request));
        } catch (RuntimeException e) {
            // Возвращаем null вместо строки, чтобы соответствовать типу ResponseEntity<UserResponse>
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.getUserById(id));
        } catch (RuntimeException e) {
            // Возвращаем null вместо строки, чтобы соответствовать типу ResponseEntity<UserResponse>
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}