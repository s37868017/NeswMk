package com.newsMk;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.newsMk.adaptor.LocalDateTimeAdapter;
import com.newsMk.dto.ArticleRequest;
import com.newsMk.dto.ArticleResponse;
import com.newsMk.service.ApiService;

import java.time.LocalDateTime;

import okhttp3.OkHttpClient;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditActivity extends AppCompatActivity {
    Button edit_btn;
    EditText editTitle,editIntro,editText;
    String id;
    private Retrofit retrofit;
    private ApiService apiService;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit);

        edit_btn=findViewById(R.id.edit_btn);
        editTitle=findViewById(R.id.editTitle);
        editIntro=findViewById(R.id.editIntro);
        editText=findViewById(R.id.editText);

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

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://**,***,**,***:8080/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        apiService = retrofit.create(ApiService.class);

        takeValues();

        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateArticle();
            }
        });
    }
    private void updateArticle(){
        String titleUpdate= editTitle.getText().toString();
        String introUpdate= editIntro.getText().toString();
        String textUpdate= editText.getText().toString();
        Long articleId= Long.parseLong(id);
        ArticleRequest request = new ArticleRequest();
        request.setTitle(titleUpdate);
        request.setIntro(introUpdate);
        request.setText(textUpdate);

        apiService.updateArticle(articleId, request).enqueue(new Callback<ArticleResponse>() {
            @Override
            public void onResponse(@NonNull Call<ArticleResponse> call, @NonNull Response<ArticleResponse> response) {
                if (response.isSuccessful()) {
                    Intent intent = new Intent(EditActivity.this, MainActivity.class);
                    startActivity(intent);
                    toastSuccesful();
                    finish();
                } else {
                    showToast();
                }
            }
            @Override
            public void onFailure(@NonNull Call<ArticleResponse> call, @NonNull Throwable t) {
                showToast();
            }
        });
    }
    private void takeValues(){
        Bundle arguments = getIntent().getExtras();
        if(arguments!=null){
            id=arguments.getString("id");
            String title= arguments.getString("title");
            String text = arguments.getString("text");
            String intro= arguments.getString("intro");
            if ((title!=null)&&(!title.isEmpty())){
                editTitle.setText(title);
            }else {
                editTitle.setHint(".......Без названия.......");
            }
            if ((intro!=null)&&(!intro.isEmpty())){
                editIntro.setText(intro);
            }else {
                editIntro.setHint(".......Нет текста.......");
            }
            if ((text!=null)&&(!text.isEmpty())) {
                editText.setText(text);
            }else {
                editText.setHint("Текст не заполнен.......");
            }
        }else {
            editTitle.setText("Пусто");
            editIntro.setText("Пусто");
            editText.setText("Пусто");
        }
    }

    private void showToast() {
        runOnUiThread(() -> Toast.makeText(EditActivity.this, "Ошибка изменения статьи", Toast.LENGTH_SHORT).show());
    }

    private void toastSuccesful() {
        runOnUiThread(() -> Toast.makeText(EditActivity.this, "Изменено", Toast.LENGTH_SHORT).show());
    }
}