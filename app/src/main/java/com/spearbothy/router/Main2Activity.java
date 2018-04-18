package com.spearbothy.router;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.spearbothy.router.annotation.Router;

@Router(path = "/main2",desc = "第二个页面")
public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }
}
