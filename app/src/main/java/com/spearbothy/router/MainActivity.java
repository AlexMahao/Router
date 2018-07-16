package com.spearbothy.router;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.spearbothy.router.annotation.Route;
import com.spearbothy.router.api.ResultCallback;
import com.spearbothy.router.api.Router;
import com.spearbothy.router.api.router.Response;

@Route(path = "/main", desc = "首页", version = "1.0.0")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 初始化module
        Router.init(getApplicationContext());
        Router.addInterceptor(new Demo1Interceptor());
        Router.addInterceptor(new Demo2Interceptor());
        // 设置debug模式
        Router.setDebug(BuildConfig.DEBUG);
    }

    public void go(View view) {
        Router.with(this)
                .url("router://app/main2")
                .start();
    }


    public void goTest(View view) {
        Router.with(this)
                .url("router://test/main")
                .start(new ResultCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(), "页面跳转成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Response response) {
                        Toast.makeText(getApplicationContext(), response.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
