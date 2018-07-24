package com.spearbothy.router;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.spearbothy.router.annotation.Autowired;
import com.spearbothy.router.annotation.Route;

@Route(path = "/main2", desc = "第二个页面", version = "1.0.0")
public class Main2Activity extends AppCompatActivity {

    @Autowired
    public int index = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }

    public void show(View view) {
        Toast.makeText(this, index + "", Toast.LENGTH_LONG).show();
    }

    public void add(View view) {
        index++;
    }
}
