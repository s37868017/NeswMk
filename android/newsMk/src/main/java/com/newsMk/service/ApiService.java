package com.newsMk.service;

import com.newsMk.dto.ArticleRequest;
import com.newsMk.dto.ArticleResponse;
import com.newsMk.dto.AuthRequest;
import com.newsMk.dto.AuthResponse;
import com.newsMk.dto.RegisterRequest;
import com.newsMk.dto.UserRequest;
import com.newsMk.dto.UserResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    @POST("/api/auth/login")
    Call<AuthResponse> login(@Body AuthRequest request);

    @POST("/api/auth/register")
    Call<Void> register(@Body RegisterRequest request);

    @GET("/api/articles")
    Call<List<ArticleResponse>> getArticles();

    @POST("/api/articles")
    Call<ArticleResponse> createArticle(@Body ArticleRequest request);

    @PUT("/api/articles/{id}")
    Call<ArticleResponse> updateArticle(@Path("id") Long id, @Body ArticleRequest request);

    @DELETE("/api/articles/{id}")
    Call<Void> deleteArticle(@Path("id") Long id);

    @GET("/api/users")
    Call<List<UserResponse>> getAllUsers();

    @PUT("/api/users/{userId}")
    Call<UserResponse> updateUser(@Path("userId") Long userId, @Body UserRequest request);

    @PUT("/api/users/{userId}/roles")
    Call<Void> updateUserRoles(@Path("userId") Long userId, @Body List<String> roleNames);

    @DELETE("/api/users/{userId}")
    Call<Void> deleteUser(@Path("userId") Long userId);
}