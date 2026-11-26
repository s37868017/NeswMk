package com.newsMk;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.newsMk.dto.RegisterRequest;
import com.newsMk.service.ApiService;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegAuth extends AppCompatActivity {

    private EditText editLogin, editEmail, editPassword;
    private Button reg_btn;
    private TextView textHint;
    private Retrofit retrofit;
    private ApiService apiService;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reg_auth);

        editLogin = findViewById(R.id.editLogin);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        reg_btn = findViewById(R.id.reg_btn);
        textHint = findViewById(R.id.textHint);

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

        reg_btn.setOnClickListener(v -> insertUser());
    }

    @SuppressLint("SetTextI18n")
    private void insertUser() {
        String login = editLogin.getText().toString();
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();

        if (login.length() <= 5) {
            editLogin.setText("");
            textHint.setText("Login должен содержать не менее 6 символов");
        } else if (email.length() <= 5 || !email.contains("@") || !email.contains(".")) {
            editEmail.setText("");
            textHint.setText("Email должен содержать не менее 6 символов, @ и .");
        } else if (password.length() <= 3) {
            editPassword.setText("");
            textHint.setText("Password должен содержать не менее 4 символов");
        } else {
            RegisterRequest request = new RegisterRequest();
            request.setLogin(login);
            request.setEmail(email);
            request.setPassword(password); // Сервер хэширует BCrypt

            apiService.register(request).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        editLogin.setText("");
                        editEmail.setText("");
                        editPassword.setText("");
                        showSuccessfull();
                        Intent intent = new Intent(RegAuth.this, Authorization.class);
                        startActivity(intent);
                    } else {
                        show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    show();
                }
            });
        }
    }

    private void show() {
        Toast.makeText(this, "Проверьте соединение", Toast.LENGTH_SHORT).show();
    }

    private void showSuccessfull() {
        Toast.makeText(this, "Успешно", Toast.LENGTH_SHORT).show();
    }
}