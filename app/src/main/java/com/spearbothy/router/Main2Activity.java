package com.spearbothy.router;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.spearbothy.router.annotation.Route;

import io.github.prototypez.savestate.core.annotation.AutoRestore;

@Route(path = "/main2", desc = "第二个页面", version = "1.0.0")
public class Main2Activity extends AppCompatActivity {

    @AutoRestore
    public String test = "123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }
}
