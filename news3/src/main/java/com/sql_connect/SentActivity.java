package com.sql_connect;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SentActivity extends AppCompatActivity {
    DataBase dataBase=new DataBase();
    private Button sent_btn;
    private EditText editTitle,editIntro,editText;
    private String table="articles";
    private String error;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());
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

        sent_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executor.execute(() -> {
                    insertData();
                    handler.post(() -> {
                        editTitle.setText("");
                        editIntro.setText("");
                        editText.setText("");
                        if(error!=null){
                            showToast();
                        }else {
                            showToastSuccessfull();
                        }
                        error=null;
                    });
                });
            }
        });
    }

    private void insertData(){
        Bundle arguments = getIntent().getExtras();
        if(arguments!=null) {
            idUserActiv = arguments.getInt("idUA");
        }
        String textTitle = String.valueOf(editTitle.getText());
        String textIntro = String.valueOf(editIntro.getText());
        String textText = String.valueOf(editText.getText());
        try {
            dataBase.insert(textTitle, textIntro, textText,idUserActiv);
        } catch (Exception e) {
            error=e.toString();
        }
    }

    private void showToast(){
        Toast.makeText(this,"Проверьте соединение",Toast.LENGTH_SHORT).show();
    }
    private void showToastSuccessfull(){
        Toast.makeText(this,"Отправлено",Toast.LENGTH_SHORT).show();
    }
}