package com.sql_connect;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Authorization extends AppCompatActivity {

    DataBaseUser dataBaseUser=new DataBaseUser();
    ExecutorService executor = Executors.newFixedThreadPool(2);
    Handler handler = new Handler(Looper.getMainLooper());
    TextView toReg,toMain;
    EditText editLogin,editPassword;
    Button auth_btn;
    private String error;
    public String login;
    private int i=0;
    String role=null;
    String[] user = null;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_authorization);

        toReg=findViewById(R.id.toReg);
        toMain=findViewById(R.id.toMain);
        editLogin=findViewById(R.id.editLogin);
        editPassword=findViewById(R.id.editPassword);
        auth_btn=findViewById(R.id.auth_btn);

        toReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent("com.sql_connect.RegAuth").setClassName(/* TODO: provide the application ID. For example: */ getPackageName(), "com.sql_connect.RegAuth");
                startActivity(intent);
            }
        });

        toMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Authorization.this, MainActivity.class);
                startActivity(intent);
            }
        });

        auth_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUser();
            }
        });
    }

    private void getUser(){
        login = String.valueOf(editLogin.getText());
        String password = String.valueOf(editPassword.getText());
        executor.execute(() -> {
            try {
                user=dataBaseUser.authUser(login, md5String(password));
            } catch (Exception e) {
                this.error = e.toString();
            }
            handler.post(() -> {
                if (error != null) {
                    show();
                } else {
                    if (user!=null) {
                        editLogin.setText("");
                        editPassword.setText("");
                        showSuccessfull();
                        Intent intent = new Intent(Authorization.this, MainActivity.class);
                        intent.putExtra("name", login);
                        intent.putExtra("id", user[0]);
                        intent.putExtra("role", user[1]);
                        startActivity(intent);
                    }else {
                        editLogin.setText("");
                        editPassword.setText("");
                        showNotUser();
                    }
                }
                error = null;
            });
        });
    }

    public static String md5String(String pass){
        MessageDigest messageDigest=null;
        byte[] digest=new byte[0];
        try {
            messageDigest=MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(pass.getBytes());
            digest=messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        BigInteger bigInteger=new BigInteger(1,digest);
        String md5hex=bigInteger.toString(16);
        while (md5hex.length()<32)
            md5hex="0"+md5hex;
        return md5hex;
    }

    private void show(){
        Toast.makeText(this,"Проверьте соединение",Toast.LENGTH_SHORT).show();
    }

    private void showSuccessfull(){
        Toast.makeText(this,"Здравствуйте: "+login,Toast.LENGTH_SHORT).show();
    }
    private void showNotUser(){
        Toast.makeText(this,"Нет такого пользователя",Toast.LENGTH_SHORT).show();
    }
}