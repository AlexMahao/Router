package com.spearbothy.router.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.spearbothy.router.R;
import com.spearbothy.router.annotation.Autowired;
import com.spearbothy.router.annotation.Route;

/**
 * @author mahao
 * @date 2018/8/15 下午7:36
 */
@Route(path = "/user_params", desc = "测试所有类型数据", version = "1.0.0")
public class UserParamsActivity extends AppCompatActivity {

    @Autowired
    public User user;


    @Autowired(enable = false)
    public int index; // 不做自动获取

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_params);
        TextView info = (TextView) findViewById(R.id.info);
        info.setText(user.toString());

        // 手动获取参数
        int index = getIntent().getIntExtra("index", 0);
        Toast.makeText(getApplicationContext(), this.index + "--" + index, Toast.LENGTH_SHORT).show();
    }
}
