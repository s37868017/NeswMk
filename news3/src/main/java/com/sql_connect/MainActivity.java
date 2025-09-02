package com.sql_connect;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity{

    Connection con;
    private String str,error;
    private static String login;
    DataBase dataBase=new DataBase();
    private List<Map<String,String>> datalist;
    private SimpleAdapter simpleAdapter;
    private ListView listView;
    public static String sItem,sTitle,sText,sCD;
    private Button toSent_btn,toUserAccaunt,toSwipe,exitUser;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView nameUser,toAdmin;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());
    private String columnTitle="title";
    private String columnIntro="intro";
    private static int idUserActiv;
    private static String role;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        toSent_btn=findViewById(R.id.toSent_btn);
        listView=findViewById(R.id.news_list);
        toUserAccaunt=findViewById(R.id.toUserAccaunt);
        toSwipe=findViewById(R.id.toSwipe);
        swipeRefreshLayout=findViewById(R.id.swipeRefreshLayout);
        nameUser=findViewById(R.id.nameUser);
        exitUser=findViewById(R.id.exitUser);
        toAdmin=findViewById(R.id.toAdmin);
        toSent_btn.setVisibility(View.INVISIBLE);
        exitUser.setVisibility(View.INVISIBLE);
        toAdmin.setVisibility(View.INVISIBLE);

        gTaI();
        Bundle arguments = getIntent().getExtras();
        if(arguments!=null) {
            login = arguments.getString("name");
            idUserActiv= Integer.parseInt(arguments.getString("id"));
            role=arguments.getString("role");
        }
        showNameRestart();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                gTaI();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        toSent_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent("com.sql_connect.SentActivity").setClassName(/* TODO: provide the application ID. For example: */ getPackageName(), "com.sql_connect.SentActivity");
                intent.putExtra("idUA",idUserActiv);
                startActivity(intent);
            }
        });

        toUserAccaunt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent("com.sql_connect.Authorization").setClassName(/* TODO: provide the application ID. For example: */ getPackageName(), "com.sql_connect.Authorization");
                startActivity(intent);
            }
        });

        toSwipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent("com.sql_connect.SwipeActivity").setClassName(/* TODO: provide the application ID. For example: */ getPackageName(), "com.sql_connect.SwipeActivity");
                startActivity(intent);
            }
        });

        exitUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login=null;
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        toAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent("com.sql_connect.AdminPanel").setClassName(/* TODO: provide the application ID. For example: */ getPackageName(), "com.sql_connect.AdminPanel");
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
    }

    private void gTaI(){
        executor.execute(() -> {
            datalist = dataBase.getData();
            error = dataBase.errorDb;
            handler.post(() -> {
                String[]from={"1","2","5","7"};
                int[]to={R.id.title_row,R.id.intro_row,R.id.cD_row,R.id.autor_row};
                simpleAdapter = new SimpleAdapter(MainActivity.this, datalist, R.layout.activity_list, from, to);
                listView.setAdapter(simpleAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        sItem= datalist.get(position).get("3");
                        sTitle= datalist.get(position).get("1");
                        sText=datalist.get(position).get("4");
                        String sIntro=datalist.get(position).get("2");
                        sCD=datalist.get(position).get("5");
                        String loginAutor=datalist.get(position).get("7");

                        Intent intent=new Intent("com.sql_connect.TextActivity").setClassName(/* TODO: provide the application ID. For example: */ getPackageName(), "com.sql_connect.TextActivity");
                        intent.putExtra("title",sTitle);
                        intent.putExtra("text",sText);
                        intent.putExtra("id",sItem);
                        intent.putExtra("intro",sIntro);
                        intent.putExtra("cD",sCD);
                        intent.putExtra("name",login);
                        intent.putExtra("loginAutor",loginAutor);
                        intent.putExtra("role",role);
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

    @SuppressLint("SetTextI18n")
    private void showNameRestart(){
        if (login!=null) {
            nameUser.setText("Здравствуйте: " + login+" "+role);
            toUserAccaunt.setVisibility(View.INVISIBLE);
            exitUser.setVisibility(View.VISIBLE);
            toSent_btn.setVisibility(View.VISIBLE);
            if (role.equals("ADMIN")){
                toAdmin.setVisibility(View.VISIBLE);
            }
        }else {
            nameUser.setText("");
            toSent_btn.setVisibility(View.INVISIBLE);
            exitUser.setVisibility(View.INVISIBLE);
            toUserAccaunt.setVisibility(View.VISIBLE);
        }
    }

    public void connect() {
            try {
                con = dataBase.Conn();
                if (con == null) {
                str = "Error";
                } else {
                str = "Connected";
                }
            }catch (Exception e) {
            throw new RuntimeException(e);
            }
            runOnUiThread(()->{
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
            });
    }

    private void showToast(){
        Toast.makeText(this,"Проверьте соединение",Toast.LENGTH_SHORT).show();
    }
}