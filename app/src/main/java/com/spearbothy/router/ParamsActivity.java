package com.spearbothy.router;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.spearbothy.router.annotation.Autowired;
import com.spearbothy.router.annotation.Route;

/**
 * 演示传值
 *  - 支持八种基本类型+String
 *  - 支持自动获取
 *  - 异常销毁和恢复
 *
 * @author mahao
 * @date 2018/7/27 上午11:38
 * @email zziamahao@163.com
 */
@Route(path = "/params", desc = "演示参数传值页面", version = "1.0.0")
public class ParamsActivity extends AppCompatActivity {

    @Autowired
    public int index = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_params);
    }

    public void show(View view) {
        Toast.makeText(this, index + "", Toast.LENGTH_LONG).show();
    }

    public void add(View view) {
        index++;
    }
}
