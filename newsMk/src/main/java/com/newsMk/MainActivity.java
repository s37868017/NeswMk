package com.newsMk;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.newsMk.adaptor.ArticleAdapter;
import com.newsMk.adaptor.LocalDateTimeAdapter;
import com.newsMk.dto.ArticleResponse;
import com.newsMk.service.ApiService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static String login, role;
    private static int idUserActiv;

    public static String sItem, sTitle, sText, sCD;

    private List<Map<String, String>> datalist = new ArrayList<>();
    private ArticleAdapter articleAdapter;
    private RecyclerView newsList;
    private FloatingActionButton toSent_btn;
    private TextView nameUser;
    private SwipeRefreshLayout swipeRefreshLayout;

    private Retrofit retrofit;
    private ApiService apiService;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Инициализация View
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        newsList = findViewById(R.id.news_list);
        toSent_btn = findViewById(R.id.toSent_btn);
        nameUser = findViewById(R.id.nameUser);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        // Настройка RecyclerView
        articleAdapter = new ArticleAdapter(this, datalist);
        newsList.setLayoutManager(new LinearLayoutManager(this));
        newsList.setAdapter(articleAdapter);

        // Клик по FAB — написать статью
        toSent_btn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SentActivity.class);
            intent.putExtra("idUA", idUserActiv);
            startActivity(intent);
        });

        // Swipe to refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            gTaI();
            swipeRefreshLayout.setRefreshing(false);
        });

        // Retrofit настройка
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

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

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://**,***,**,***:8080/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        apiService = retrofit.create(ApiService.class);

        // Загрузка данных
        gTaI();

        // Получаем данные из интента (при логине)
        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            login = arguments.getString("name");
            idUserActiv = Integer.parseInt(arguments.getString("id"));
            role = arguments.getString("role");
        }

        // Обновляем UI в зависимости от авторизации
        showNameRestart();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Логика при нажатии кнопки "Назад"
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        gTaI(); // Обновляем список при возврате на экран
        showNameRestart(); // Обновляем приветствие и меню
    }

    private void gTaI() {
        apiService.getArticles().enqueue(new Callback<List<ArticleResponse>>() {
            @Override
            public void onResponse(Call<List<ArticleResponse>> call, Response<List<ArticleResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ArticleResponse> articles = response.body();

                    datalist.clear();

                    List<ArticleResponse> reversed = new ArrayList<>(articles);
                    Collections.reverse(reversed);

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

                    for (ArticleResponse article : reversed) {
                        Map<String, String> map = new HashMap<>();
                        map.put("3", article.getId().toString());
                        map.put("1", article.getTitle());
                        map.put("2", article.getIntro());
                        map.put("4", article.getText());
                        String formattedDate = "";
                        try {
                            LocalDateTime dateTime = LocalDateTime.parse(article.getCreatedAt());
                            formattedDate = dateTime.format(formatter);
                        } catch (Exception e) {
                            formattedDate = article.getCreatedAt();
                        }
                        map.put("5", formattedDate);
                        map.put("7", article.getUserId().toString());
                        map.put("8", article.getUserName());
                        datalist.add(map);
                    }

                    // Обновляем адаптер
                    articleAdapter.notifyDataSetChanged();

                    // Обработчик клика по элементу
                    articleAdapter.setOnItemClickListener(position -> {
                        Map<String, String> item = datalist.get(position);
                        sItem = item.get("3");
                        sTitle = item.get("1");
                        sText = item.get("4");
                        sCD = item.get("5");

                        Intent intent = new Intent(MainActivity.this, TextActivity.class);
                        intent.putExtra("title", sTitle);
                        intent.putExtra("text", sText);
                        intent.putExtra("id", sItem);
                        intent.putExtra("intro", item.get("2"));
                        intent.putExtra("cD", sCD);
                        intent.putExtra("name", login);
                        intent.putExtra("loginAutor", item.get("8"));
                        intent.putExtra("role", role);
                        startActivity(intent);
                    });

                } else {
                    Toast.makeText(MainActivity.this, "Ошибка загрузки: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ArticleResponse>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Проверьте соединение", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String textRole() {
        switch (role) {
            case "ROLE_ADMIN":
                return "роль: admin";
            case "ROLE_REDACTOR":
                return "роль: redactor";
            case "ROLE_USER":
                return "роль: user";
            default:
                return "";
        }
    }

    private void showNameRestart() {
        if (login != null) {
            nameUser.setText("Здравствуйте: " + login + " " + textRole());
            toSent_btn.setVisibility(View.VISIBLE);
        } else {
            nameUser.setText("Гость");
            toSent_btn.setVisibility(View.GONE);
        }
        invalidateOptionsMenu(); // Обновляем меню
    }

    // ==================== Меню Toolbar ====================
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_login).setVisible(login == null);
        menu.findItem(R.id.action_logout).setVisible(login != null);
        menu.findItem(R.id.action_admin).setVisible(login != null && "ROLE_ADMIN".equals(role));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_login) {
            startActivity(new Intent(this, Authorization.class));

        } else if (id == R.id.action_logout) {
            // Очищаем токен
            getSharedPreferences("auth", MODE_PRIVATE)
                    .edit()
                    .remove("*********")
                    .apply();

            // Сбрасываем статические переменные
            login = null;
            role = null;
            idUserActiv = 0;

            // Полностью перезапускаем MainActivity как новую
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        } else if (id == R.id.action_admin) {
            startActivity(new Intent(this, AdminPanel.class));
        }

        return super.onOptionsItemSelected(item);
    }
}