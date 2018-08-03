package com.spearbothy.router.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.spearbothy.router.R;
import com.spearbothy.router.annotation.Route;

/**
 * @author mahao
 * @date 2018/8/3 上午10:52
 * @email zziamahao@163.com
 */

@Route(path = "/request_code", desc = "首页", version = "1.0.0")
public class RequestCodeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_code);
    }

    public void resultOk(View view) {
        setResult(RESULT_OK);
        finish();
    }
}
