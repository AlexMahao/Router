package com.spearbothy.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.spearbothy.router.annotation.Route;

@Route(path = "/main", desc = "module 测试页面", version = "1.0.0")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }
}
