package com.sql_connect;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TextActivity extends AppCompatActivity {

    DataBase dataBase=new DataBase();
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());
    private TextView textArticle,titlePage,cD_row,toAutorization;
    private String error,text,title,intro,cD;
    public static String id;
    private Button buttonEdit,buttonDelete;
    private static String login;

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

        takeName();
        takeValues();

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executor.execute(() -> {
                    deleteArticle();
                     handler.post(() -> {
                         if(error!=null){
                             showToast();
                         }else {
                             Intent intent = new Intent(TextActivity.this, MainActivity.class);
                             startActivity(intent);
                             showToastSuccessfull();
                             finish();
                         }
                         error=null;
                     });
                });
            }
        });

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent("com.sql_connect.EditActivity").setClassName(/* TODO: provide the application ID. For example: */ getPackageName(), "com.sql_connect.EditActivity");
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
            if ((role.equals("ADMIN"))||(role.equals("REDACTOR"))||(login.equals(loginAutor))) {
                buttonEdit.setVisibility(View.VISIBLE);
                buttonDelete.setVisibility(View.VISIBLE);
            }
        } else {
            toAutorization.setVisibility(View.VISIBLE);
        }
    }

    private void deleteArticle(){
        try {
            dataBase.deleteArticle();
        }catch (Exception ex){
            String error=ex.toString();
            this.error=error;
        }
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
            cD_row.setText(cD);
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