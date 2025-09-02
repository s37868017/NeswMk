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

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.security.MessageDigest;
import java.sql.Connection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegAuth extends AppCompatActivity {

    DataBaseUser dataBaseUser=new DataBaseUser();
    ExecutorService executor = Executors.newSingleThreadExecutor();
//    ExecutorService executor = Executors.newFixedThreadPool(2);
    Handler handler = new Handler(Looper.getMainLooper());
    private EditText editLogin,editEmail,editPassword;
    private Button reg_btn;
    private TextView textHint;
    private String error,errorE;
    private int i=0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reg_auth);

        editLogin=findViewById(R.id.editLogin);
        editEmail=findViewById(R.id.editEmail);
        editPassword=findViewById(R.id.editPassword);
        reg_btn=findViewById(R.id.reg_btn);
        textHint=findViewById(R.id.textHint);

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    insertUser();
            }
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

    @SuppressLint("SetTextI18n")
    private void insertUser(){
        String login = String.valueOf(editLogin.getText());
        String email = String.valueOf(editEmail.getText());
        String password = String.valueOf(editPassword.getText());

        if (login.length()<=5){
            editLogin.setText("");
            textHint.setText("Login должен содержать не менее 6 символов");
        }else if (email.length()<=5||!email.contains("@")||!email.contains(".")){
            editEmail.setText("");
            textHint.setText("Email должен содержать не менее 6 символов, @ и .");
        }else if (password.length()<=3){
            editPassword.setText("");
            textHint.setText("Password должен содержать не менее 4 символов");
        }else {
            executor.execute(() -> {
                try {
                    i = dataBaseUser.isExistUser(login);
                } catch (Exception ex) {
                    this.errorE = ex.toString();
                }
                handler.post(() -> {
                    if (errorE != null) {
                        show();
                        errorE = null;
                    } else {
                        if (i != 0) {
                            editLogin.setText("");
                            textHint.setText("Login уже занят, выберите другой");
                        } else {
                            executor.execute(() -> {
                                try {
                                    dataBaseUser.insertUser(login, email, md5String(password));
                                } catch (Exception e) {
                                    this.error = e.toString();
                                }
                                handler.post(() -> {
                                    if (error != null) {
                                        show();
                                    } else {
                                        editLogin.setText("");
                                        editEmail.setText("");
                                        editPassword.setText("");
                                        showSuccessfull();
                                        Intent intent = new Intent(RegAuth.this, Authorization.class);
                                        startActivity(intent);
                                    }
                                    error = null;
                                });
                            });
                        }
                    }
                });
            });
        }
    }

    public void connect() {
        Connection con;
        try {
            con = dataBaseUser.Conn();
            con.close();
        }catch (Exception e) {
            this.error = e.toString();
        }
    }

    private void show(){
        Toast.makeText(this,"Проверьте соединение",Toast.LENGTH_SHORT).show();
    }

    private void newShow(){
        String s= String.valueOf(i);
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }

    private void showSuccessfull(){
        Toast.makeText(this,"Успешно",Toast.LENGTH_SHORT).show();
    }
}