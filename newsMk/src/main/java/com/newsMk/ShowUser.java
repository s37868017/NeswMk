package com.newsMk;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.newsMk.dto.ArticleResponse;
import com.newsMk.dto.UserRequest;
import com.newsMk.dto.UserResponse;
import com.newsMk.service.ApiService;
import com.newsMk.adaptor.PublicationAdapter;

import okhttp3.OkHttpClient;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowUser extends AppCompatActivity {
    private String login,roleUser,role,email;
    private TextView showLogin,showEmail,showRole,articlesString;
    private Button delete_btn,edit_btn;
    private RecyclerView publication_list;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Spinner spinnerDialog;
    private Retrofit retrofit;
    private ApiService apiService;
    private Long idUser;
    private PublicationAdapter publicationAdapter;
    private List<Map<String, String>> publicationList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_user);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        showLogin=findViewById(R.id.showLogin);
        showEmail=findViewById(R.id.showEmail);
        showRole=findViewById(R.id.showRole);
        articlesString=findViewById(R.id.articles);
        delete_btn=findViewById(R.id.delete_btn);
        edit_btn=findViewById(R.id.editUser_btn);
        publication_list=findViewById(R.id.publication_list);
        swipeRefreshLayout=findViewById(R.id.swipeRefreshLayout);
        spinnerDialog=findViewById(R.id.spinnerDialog);

        publicationAdapter = new PublicationAdapter(this, publicationList);
        publication_list.setLayoutManager(new LinearLayoutManager(this));
        publication_list.setAdapter(publicationAdapter);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    String token = getSharedPreferences("auth", MODE_PRIVATE).getString("*********", null);
                    Request request = chain.request();
                    if (token != null) {
                        request = request.newBuilder()
                                .header("Authorization", "Bearer " + token)
                                .build();
                    }
                    return chain.proceed(request);
                })
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://**,***,**,***:8080/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        Bundle arguments = getIntent().getExtras();
        if(arguments!=null) {
            idUser= Long.parseLong(arguments.getString("id"));
            login = arguments.getString("login");
            roleUser = arguments.getString("roles");
            email = arguments.getString("email");
        }

        showLogin.setText(login);
        showEmail.setText(email);
        showRole.setText(roleUser);

        getDataOneUser();

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser();
            }
        });

        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDataOneUser();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void getDataOneUser() {
        apiService.getArticles().enqueue(new Callback<List<ArticleResponse>>() {
            @Override
            public void onResponse(Call<List<ArticleResponse>> call, Response<List<ArticleResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ArticleResponse> articles = response.body();
                    publicationList.clear();

                    List<ArticleResponse> reversed = new ArrayList<>(articles);
                    Collections.reverse(reversed);

                    for (ArticleResponse article : reversed) {
                        if (article.getUserId() != null && article.getUserId().equals(idUser)) {
                            Map<String, String> map = new HashMap<>();
                            map.put("id", article.getId().toString());
                            map.put("title", article.getTitle());
                            map.put("intro", article.getIntro());
                            map.put("text", article.getText());
                            map.put("createdate", article.getCreatedAt().toString());

                            publicationList.add(map);
                        }
                    }

                    if (!publicationList.isEmpty()) {
                        publicationAdapter.notifyDataSetChanged();

                        publicationAdapter.setOnItemClickListener(position -> {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
                            try {
                                Map<String, String> selectedItem = publicationList.get(position);

                                String idA = selectedItem.get("id");
                                String title = selectedItem.get("title");
                                String intro = selectedItem.get("intro");
                                String text = selectedItem.get("text");
                                String date = selectedItem.get("createdate");

                                Intent intent = new Intent(ShowUser.this, TextActivity.class);
                                intent.putExtra("id", idA);
                                intent.putExtra("title", title);
                                intent.putExtra("intro", intro);
                                intent.putExtra("text", text);
                                String formattedDate = "";
                                try {
                                    LocalDateTime dateTime = LocalDateTime.parse(date);
                                    formattedDate = dateTime.format(formatter);
                                } catch (Exception e) {
                                    formattedDate = date;
                                }
                                intent.putExtra("cD", formattedDate);
                                intent.putExtra("role", "ROLE_ADMIN");
                                intent.putExtra("name", login);

                                startActivity(intent);
                            } catch (Exception e) {
                                Log.e("ShowUser", "Error opening article", e);
                                Toast.makeText(ShowUser.this, "Ошибка при открытии статьи: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        articlesString.setText("Пользователем не добавлено публикаций");
                        publicationAdapter.notifyDataSetChanged();
                    }
                } else {
                    showToastCheckConnect();
                }
            }

            @Override
            public void onFailure(Call<List<ArticleResponse>> call, Throwable t) {
                showToastCheckConnect();
            }
        });
    }

    private void deleteUser(){
        apiService.deleteUser(idUser).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Intent intent = new Intent(ShowUser.this, AdminPanel.class);
                    startActivity(intent);
                    showToastDeleted();
                    finish();
                } else {
                    showElse();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showOnFailure();
            }
        });
    }

    private void updateUserWithRoles(String username, String email, String selectedRole) {
        UserRequest request = new UserRequest();
        request.setLogin(username);
        request.setEmail(email);

        List<String> roleNames = new ArrayList<>();
        roleNames.add(selectedRole);

        apiService.updateUser(idUser, request).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful()) {

                    if (!selectedRole.equals("Выберите")) {
                        // Сначала обновили профиль → теперь роль
                        updateUserRolesAfterSuccess(roleNames);
                    }else {
                        Toast.makeText(ShowUser.this, "Изменены данные без роли", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(ShowUser.this, AdminPanel.class));
                        finish();
                    }

                } else {
                    Toast.makeText(ShowUser.this, "Ошибка обновления данных", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                showOnFailure();
            }
        });
    }

    private void updateUserRolesAfterSuccess(List<String> roleNames) {
        apiService.updateUserRoles(idUser, roleNames).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    showToastEdited();
                    startActivity(new Intent(ShowUser.this, AdminPanel.class));
                    finish();
                } else {
                    startActivity(new Intent(ShowUser.this, AdminPanel.class));
                    finish();
                    Toast.makeText(ShowUser.this, "Профиль обновлён, без изменения роли", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showOnFailure1();
            }
        });
    }

    private void showEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Редактировать пользователя");
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_dialog_edit_user, null);
        builder.setView(dialogView);
        EditText editLogin = dialogView.findViewById(R.id.editLogin);
        EditText editEmail = dialogView.findViewById(R.id.editEmail);
        spinnerDialog=dialogView.findViewById(R.id.spinnerDialog);
        editLogin.setText(login);
        editEmail.setText(email);
        spinnerDialogFunc();
        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            String newLogin = editLogin.getText().toString().trim();
            String newEmail = editEmail.getText().toString().trim();
            String selectedRole = spinnerDialog.getSelectedItem().toString();
            updateUserWithRoles(newLogin,newEmail,selectedRole);
        });
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void spinnerDialogFunc() {
        ArrayAdapter<Enum> roles = new ArrayAdapter<Enum>(this, android.R.layout.simple_spinner_item,UserRole.values());
        roles.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDialog.setAdapter(roles);
        spinnerDialog.setPrompt("Выберите роль для пользователя");
        spinnerDialog.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                role=parent.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showToastDeleted(){
        Toast.makeText(this,"Удалено",Toast.LENGTH_SHORT).show();
    }
    private void showToastEdited(){
        Toast.makeText(this,"Изменено",Toast.LENGTH_SHORT).show();
    }
    private void showOnFailure(){
        Toast.makeText(this, "Что то пошло не так при изменении данных пользователя", Toast.LENGTH_SHORT).show();
    }

    private void showElse(){
        Toast.makeText(this, "Не получилось удалить пользователя", Toast.LENGTH_SHORT).show();
    }

    private void showOnFailure1(){
        Toast.makeText(this, "Что то пошло не так при обновлении роли пользователя", Toast.LENGTH_SHORT).show();
    }

    private void showToastCheckConnect() {
        Toast.makeText(this, "Проверьте соединение", Toast.LENGTH_SHORT).show();
    }
}