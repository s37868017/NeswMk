package com.newsMk;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.newsMk.dto.AuthRequest;
import com.newsMk.dto.AuthResponse;
import com.newsMk.service.ApiService;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Authorization extends AppCompatActivity {

    private TextView toReg;
    private EditText editLogin, editPassword;
    private Button auth_btn;
    public String login;
    private String role = null;

    private Retrofit retrofit;
    private ApiService apiService;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_authorization);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toReg = findViewById(R.id.toReg);
        editLogin = findViewById(R.id.editLogin);
        editPassword = findViewById(R.id.editPassword);
        auth_btn = findViewById(R.id.auth_btn);

        // Настройка логирования Retrofit
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging) // Добавляем логирование
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

        toReg.setOnClickListener(v -> {
            Intent intent = new Intent("com.newsMk.RegAuth").setClassName(getPackageName(), "com.newsMk.RegAuth");
            startActivity(intent);
        });

        auth_btn.setOnClickListener(v -> getUser());
    }

    private void getUser() {
        login = editLogin.getText().toString();
        String password = editPassword.getText().toString();
        AuthRequest request = new AuthRequest();
        request.setLogin(login);
        request.setPassword(password);

        apiService.login(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();
                    Long id = response.body().getId(); // Получаем id
                    role = response.body().getRole(); // Получаем роль
                    getSharedPreferences("auth", MODE_PRIVATE)
                            .edit()
                            .putString("*********", token)
                            .apply();

                    editLogin.setText("");
                    editPassword.setText("");
                    showSuccessfull();
                    Intent intent = new Intent(Authorization.this, MainActivity.class);
                    intent.putExtra("name", login);
                    intent.putExtra("id", id.toString());
                    intent.putExtra("role", role);
                    startActivity(intent);
                } else {
                    editLogin.setText("");
                    editPassword.setText("");
                    showNotUser();
                    Log.e("Authorization", "Login failed with code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                show();
                Log.e("Authorization", "Login failed: " + t.getMessage());
            }
        });
    }

    private void show() {
        Toast.makeText(this, "Проверьте соединение", Toast.LENGTH_SHORT).show();
    }

    private void showSuccessfull() {
        Toast.makeText(this, "Здравствуйте: " + login, Toast.LENGTH_SHORT).show();
    }

    private void showNotUser() {
        Toast.makeText(this, "Нет такого пользователя", Toast.LENGTH_SHORT).show();
    }
}