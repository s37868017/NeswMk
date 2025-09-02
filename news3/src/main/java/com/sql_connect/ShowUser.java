package com.sql_connect;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ShowUser extends AppCompatActivity {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());
    DataBaseUser dataBaseUser=new DataBaseUser();
    private SimpleAdapter simpleAdapter;
    private String error,login,roleUser,role,email,newLogin,newEmail;
    private TextView showLogin,showEmail,showRole;
    private Button delete_btn,edit_btn;
    private List<Map<String,String>> oneUser;
    private ListView publication_list;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Spinner spinnerDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_user);

        showLogin=findViewById(R.id.login);
        showEmail=findViewById(R.id.email);
        showRole=findViewById(R.id.role);
        delete_btn=findViewById(R.id.delete_btn);
        edit_btn=findViewById(R.id.editUser_btn);
        publication_list=findViewById(R.id.publication_list);
        swipeRefreshLayout=findViewById(R.id.swipeRefreshLayout);
        spinnerDialog=findViewById(R.id.spinnerDialog);

        Bundle arguments = getIntent().getExtras();
        if(arguments!=null) {
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
                executor.execute(() -> {
                    deleteUser();
                    handler.post(() -> {
                        if(error!=null){
                            showToastCheckConnect();
                        }else {
                            Intent intent = new Intent(ShowUser.this, AdminPanel.class);
                            startActivity(intent);
                            showToastDeleted();
                            finish();
                        }
                        error=null;
                    });
                });
            }
        });

        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ShowUser.this, AdminPanel.class);
        startActivity(intent);
        finish();
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

    private void getDataOneUser(){
        executor.execute(() -> {
            oneUser = dataBaseUser.getDataOneUser(login);
            error = dataBaseUser.errorDb;
            handler.post(() -> {
                if(!oneUser.isEmpty()) {
                    String[] from = {"title", "intro"};
                    int[] to = {R.id.title_row, R.id.intro_row};
                    simpleAdapter = new SimpleAdapter(ShowUser.this, oneUser, R.layout.activity_list_publication, from, to);
                    publication_list.setAdapter(simpleAdapter);

                    publication_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String idA = oneUser.get(position).get("id");
                            String title = oneUser.get(position).get("title");
                            String intro = oneUser.get(position).get("intro");
                            String text = oneUser.get(position).get("text");
                            String createdate = oneUser.get(position).get("createdate");
                            String user = oneUser.get(position).get("user");
                            String role = "ADMIN";
                            String login = "rewrite";

                            Intent intent = new Intent("com.sql_connect.TextActivity").setClassName(/* TODO: provide the application ID. For example: */ getPackageName(), "com.sql_connect.TextActivity");
                            intent.putExtra("id", idA);
                            intent.putExtra("title", title);
                            intent.putExtra("intro", intro);
                            intent.putExtra("text", text);
                            intent.putExtra("cD", createdate);
                            intent.putExtra("user", user);
                            intent.putExtra("role", role);
                            intent.putExtra("name", login);
                            startActivity(intent);
                        }
                    });
                }
                if(error!=null){
                    showToastCheckConnect();
                    error=null;
                }
            });
        });
    }

    private void deleteUser(){
        try {
            dataBaseUser.deleteUser(login);
        }catch (Exception ex){
            error=ex.toString();
        }
    }

    private void editUser(){
        executor.execute(() -> {
        try {
            dataBaseUser.editUser(login,newLogin,newEmail,role);
        }catch (Exception ex){
            error=ex.toString();
        }
            handler.post(() -> {
                if(error!=null){
                    showToastCheckConnect();
                }else {
                    Intent intent = new Intent(ShowUser.this, AdminPanel.class);
                    startActivity(intent);
                    showToastEdited();
                    finish();
                }
                error=null;
            });
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
            newLogin = editLogin.getText().toString();
            newEmail = editEmail.getText().toString();
            editUser();
        });

        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showToastDeleted(){
        Toast.makeText(this,"Удалено",Toast.LENGTH_SHORT).show();
    }

    private void showToastEdited(){
        Toast.makeText(this,"Изменено",Toast.LENGTH_SHORT).show();
    }

    private void showToastCheckConnect() {
        Toast.makeText(this, "Проверьте соединение", Toast.LENGTH_SHORT).show();
    }
}