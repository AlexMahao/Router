package com.spearbothy.router;

import android.app.Application;

import com.spearbothy.router.api.Router;
import com.spearbothy.router.sample.interceptor.LoginInterceptor;

/**
 * @author mahao
 * @date 2018/7/31 下午3:42
 * @email zziamahao@163.com
 */

public class App extends Application{

    public static final boolean sIsLogin = false;

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化
        Router.init(getApplicationContext());
        // 设置debug模式
        Router.setDebug(BuildConfig.DEBUG);
        // 添加登录拦截
        Router.addInterceptor(new LoginInterceptor());
        // 暂时不做参数校验，没想好怎么判断
//        Router.addInterceptor(new VersionInterceptor());
    }
}
