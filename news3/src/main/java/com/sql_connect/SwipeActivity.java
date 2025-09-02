package com.sql_connect;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class SwipeActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView number;
    private int n;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_swipe);

        swipeRefreshLayout=findViewById(R.id.swipeRefreshLayout);
        number=findViewById(R.id.number);

        number.setText("0");

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                nextOne();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void  nextOne(){
        n++;
        String sn= String.valueOf(n);
        number.setText(sn);
    }
}