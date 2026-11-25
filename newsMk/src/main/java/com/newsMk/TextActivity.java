package com.newsMk;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.newsMk.service.ApiService;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TextActivity extends AppCompatActivity {
    private TextView textArticle,titlePage,cD_row,toAutorization;
    private String text,title,intro,cD;
    public static String id;
    private Button buttonEdit,buttonDelete;
    private static String login;
    private Retrofit retrofit;
    private ApiService apiService;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_text);

        textArticle=findViewById(R.id.textArticle);
        titlePage=findViewById(R.id.titlePage);
        buttonEdit=findViewById(R.id.buttonEdit);
        buttonDelete=findViewById(R.id.buttonDelete);
        cD_row=findViewById(R.id.cD_row);
        toAutorization=findViewById(R.id.toAutorization);
        buttonEdit.setVisibility(View.INVISIBLE);
        buttonDelete.setVisibility(View.INVISIBLE);

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

        takeName();
        takeValues();

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteArticle();
            }
        });

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent("com.newsMk.EditActivity").setClassName(getPackageName(), "com.newsMk.EditActivity");
                intent.putExtra("id",id);
                intent.putExtra("title",title);
                intent.putExtra("intro",intro);
                intent.putExtra("text",text);
                startActivity(intent);
                finish();
            }
        });

        toAutorization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TextActivity.this, Authorization.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void takeName() {
        Bundle arguments = getIntent().getExtras();
        login = arguments.getString("name");
        String loginAutor = arguments.getString("loginAutor");
        String role=arguments.getString("role");
        if (login != null) {
            toAutorization.setVisibility(View.INVISIBLE);
            if (("ROLE_ADMIN".equals(role))||("ROLE_REDACTOR".equals(role))||(login.equals(loginAutor))) {
                buttonEdit.setVisibility(View.VISIBLE);
                buttonDelete.setVisibility(View.VISIBLE);
            }
        } else {
            toAutorization.setVisibility(View.VISIBLE);
        }
    }

    private void deleteArticle(){
        apiService.deleteArticle(Long.parseLong(id)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.isSuccessful()) {
                    Intent intent = new Intent(TextActivity.this, MainActivity.class);
                    startActivity(intent);
                    showToastSuccessfull();
                    finish();
                } else {
                    showToast();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showToast();
            }
        });
    }

    private void takeValues(){
        Bundle arguments = getIntent().getExtras();
        if(arguments!=null){
            id=arguments.getString("id");
            title = arguments.get("title").toString();
            text = arguments.getString("text");
            intro= arguments.getString("intro");
            cD= arguments.getString("cD");
            if ((title!=null)&&(!title.isEmpty())){
                titlePage.setText(title);
            }else {
                titlePage.setTextSize(13);
                titlePage.setText(".......Без названия.......");
            }
            if ((text!=null)&&(!text.isEmpty())) {
                textArticle.setText(text);
            }else {
                textArticle.setTextSize(10);
                textArticle.setText("Текст не заполнен.......");
            }
            cD_row.setText("Добавлено: "+cD);
        }else {
            titlePage.setText("Пусто");
            textArticle.setText("Пусто");
        }
    }

    private void showToast(){
        Toast.makeText(this,"Проверьте соединение",Toast.LENGTH_SHORT).show();
    }

    private void showToastSuccessfull(){
        Toast.makeText(this,"Удалено",Toast.LENGTH_SHORT).show();
    }
}