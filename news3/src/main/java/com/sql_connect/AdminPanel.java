package com.sql_connect;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminPanel extends AppCompatActivity {

    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());
    private SwipeRefreshLayout swipeRefreshLayout;
    private SimpleAdapter simpleAdapter;
    private List<Map<String,String>> userlist;
    DataBaseUser dataBaseUser=new DataBaseUser();
    private String error;
    private ListView listView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_panel);

        swipeRefreshLayout=findViewById(R.id.swipeRefreshLayout);
        listView=findViewById(R.id.users_list);

        getAllUsers();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllUsers();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AdminPanel.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void getAllUsers(){
        executor.execute(() -> {
            userlist = dataBaseUser.getAllUsers();
            error = dataBaseUser.errorDb;
            handler.post(() -> {
                String[]from={"2","count","roles"};
                int[]to={R.id.userName_row,R.id.number_row,R.id.roleUser_row};
                simpleAdapter = new SimpleAdapter(AdminPanel.this, userlist, R.layout.activity_list_user, from, to);
                listView.setAdapter(simpleAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String login=userlist.get(position).get("2");
                        String count=userlist.get(position).get("count");
                        String roles=userlist.get(position).get("roles");
                        String email=userlist.get(position).get("email");

                        Intent intent=new Intent("com.sql_connect.ShowUser").setClassName(/* TODO: provide the application ID. For example: */ getPackageName(), "com.sql_connect.ShowUser");
                        intent.putExtra("login",login);
                        intent.putExtra("count",count);
                        intent.putExtra("roles",roles);
                        intent.putExtra("email",email);
                        startActivity(intent);
                    }
                });
                if(error!=null){
                    showToast();
                    error=null;
                }
            });
        });
    }

    private void showToast() {
        Toast.makeText(this, "Проверьте соединение", Toast.LENGTH_SHORT).show();
    }
}