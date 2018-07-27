package com.spearbothy.router;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.spearbothy.router.annotation.Route;

/**
 * @author mahao
 * @date 2018/7/27 下午4:51
 * @email zziamahao@163.com
 */
@Route(path = "/login", desc = "登录页面", version = "1.0.0")
public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
}
