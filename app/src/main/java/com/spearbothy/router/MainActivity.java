package com.spearbothy.router;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.spearbothy.router.annotation.Router;
import com.spearbothy.router.api.ResultCallback;
import com.spearbothy.router.api.RouterClient;
import com.spearbothy.router.api.RouterResponse;

@Router(path = "/main", desc = "首页")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RouterClient.init(getPackageName());
        // 添加测试模块
        RouterClient.addModule("com.spearbothy.test");
    }

    public void go(View view) {
        RouterClient.with(this)
                .url("router://com.spearbothy.router/main2")
                .start();
    }


    public void goTest(View view) {
        RouterClient.with(this)
                .url("router://com.spearbothy.test/main")
                .start(new ResultCallback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(RouterResponse response) {
                        Toast.makeText(getApplicationContext(), response.getDesc(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
