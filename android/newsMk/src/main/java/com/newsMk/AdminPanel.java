package com.newsMk;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.newsMk.adaptor.UserAdapter;
import com.newsMk.dto.UserResponse;
import com.newsMk.service.ApiService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdminPanel extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView usersList;
    private UserAdapter userAdapter;
    private List<Map<String, String>> userlist = new ArrayList<>();

    private Retrofit retrofit;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_panel);

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        usersList = findViewById(R.id.users_list);

        // Настройка RecyclerView
        userAdapter = new UserAdapter(this, userlist);
        usersList.setLayoutManager(new LinearLayoutManager(this));
        usersList.setAdapter(userAdapter);

        // Retrofit
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    String token = getSharedPreferences("auth", MODE_PRIVATE).getString("*********", null);
                    if (token != null) {
                        return chain.proceed(chain.request().newBuilder()
                                .header("Authorization", "Bearer " + token)
                                .build());
                    }
                    return chain.proceed(chain.request());
                })
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://**,***,**,***:8080/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        // Загрузка данных
        getAllUsers();

        // Swipe to refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            getAllUsers();
            swipeRefreshLayout.setRefreshing(false);
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Логика при нажатии кнопки "Назад"
                Intent intent = new Intent(AdminPanel.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllUsers(); // Обновляем при возврате
    }

    private void getAllUsers() {
        apiService.getAllUsers().enqueue(new Callback<List<UserResponse>>() {
            @Override
            public void onResponse(Call<List<UserResponse>> call, Response<List<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<UserResponse> users = response.body();
                    List<Map<String, String>> newList = new ArrayList<>();

                    for (UserResponse user : users) {
                        Map<String, String> map = new HashMap<>();
                        map.put("id", String.valueOf(user.getId()));
                        map.put("login", user.getLogin());
                        map.put("count", user.getArticlesCount().toString());
                        map.put("roles", user.getRoles().toString().replace("[", "").replace("]", ""));
                        map.put("email", user.getEmail());
                        newList.add(map);
                    }

                    userAdapter.updateData(newList);
                } else {
                    showNotUsers();
                }
            }

            @Override
            public void onFailure(Call<List<UserResponse>> call, Throwable t) {
                showToast();
            }
        });
    }

    private void showToast() {
        Toast.makeText(this, "Проверьте соединение", Toast.LENGTH_SHORT).show();
    }

    private void showNotUsers() {
        Toast.makeText(this, "Нет пользователей", Toast.LENGTH_SHORT).show();
    }
}