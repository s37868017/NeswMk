package com.sql_connect;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditActivity extends AppCompatActivity {

    DataBase dataBase=new DataBase();
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());
    Button edit_btn;
    EditText editTitle,editIntro,editText;

    String id,error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit);

        edit_btn=findViewById(R.id.edit_btn);
        editTitle=findViewById(R.id.editTitle);
        editIntro=findViewById(R.id.editIntro);
        editText=findViewById(R.id.editText);

        takeValues();

        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executor.execute(() -> {
                    updateArticle();
                    handler.post(() -> {
                        if(error!=null){
                            showToast();
                        }else {
                            Intent intent = new Intent(EditActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        error=null;
                    });
                });
            }
        });
    }

    private void updateArticle(){
        String titleUpdate= String.valueOf(editTitle.getText());
        String introUpdate= String.valueOf(editIntro.getText());
        String textUpdate= String.valueOf(editText.getText());
        try {
            dataBase.editArticle(titleUpdate,introUpdate,textUpdate,id);
        }catch (Exception ex){
            error=ex.toString();
        }
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

    private void showToast(){
        Toast.makeText(this,id,Toast.LENGTH_SHORT).show();
    }
}