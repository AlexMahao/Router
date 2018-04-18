package com.spearbothy.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.spearbothy.router.annotation.Router;

@Router(path = "/main")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }
}
