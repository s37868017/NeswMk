package com.newsMk;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.newsMk.dto.ArticleRequest;
import com.newsMk.dto.ArticleResponse;
import com.newsMk.service.ApiService;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class SentActivity extends AppCompatActivity {
    private Button sent_btn;
    private EditText editTitle,editIntro,editText;
    private Retrofit retrofit;
    private ApiService apiService;
    private int idUserActiv;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sent);
        editTitle=findViewById(R.id.editTitle);
        editIntro=findViewById(R.id.editIntro);
        editText=findViewById(R.id.editText);
        sent_btn=findViewById(R.id.sent_btn);
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
        sent_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertData();
            }
        });
    }
    private void insertData(){
        Bundle arguments = getIntent().getExtras();
        if(arguments!=null) {
            idUserActiv = arguments.getInt("idUA");
        }
        String textTitle = editTitle.getText().toString();
        String textIntro = editIntro.getText().toString();
        String textText = editText.getText().toString();
        ArticleRequest request = new ArticleRequest();
        request.setTitle(textTitle);
        request.setIntro(textIntro);
        request.setText(textText);
        apiService.createArticle(request).enqueue(new Callback<ArticleResponse>() {
            @Override
            public void onResponse(Call<ArticleResponse> call, Response<ArticleResponse> response) {
                if (response.isSuccessful()) {
                    editTitle.setText("");
                    editIntro.setText("");
                    editText.setText("");
                    showToastSuccessfull();
                    startActivity(new Intent(SentActivity.this, MainActivity.class));
                    finish();
                } else {
                    showToast();
                }
            }
            @Override
            public void onFailure(Call<ArticleResponse> call, Throwable t) {
                showToast();
            }
        });
    }
    private void showToast(){
        Toast.makeText(this,"Проверьте соединение",Toast.LENGTH_SHORT).show();
    }
    private void showToastSuccessfull(){
        Toast.makeText(this,"Отправлено",Toast.LENGTH_SHORT).show();
    }
}