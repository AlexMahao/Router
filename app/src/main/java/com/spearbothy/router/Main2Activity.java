package com.spearbothy.router;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.spearbothy.router.annotation.Route;

@Route(path = "/main2", desc = "第二个页面", version = "1.0.0")
public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }
}
