package com.spearbothy.router;

import android.app.Application;

import com.alibaba.fastjson.JSON;
import com.spearbothy.router.api.Router;
import com.spearbothy.router.api.router.AutowiredJsonAdapter;
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
        Router.init(getApplicationContext(), new SimpleJsonAdapter());
        // 设置debug模式
        Router.setDebug(BuildConfig.DEBUG);
        // 添加登录拦截
        Router.addInterceptor(new LoginInterceptor());
        // 暂时不做参数校验，没想好怎么判断
//        Router.addInterceptor(new VersionInterceptor());
    }


    class SimpleJsonAdapter implements AutowiredJsonAdapter {

        @Override
        public <T> T JSON2Object(String s, Class<T> clazz) {
            return JSON.parseObject(s, clazz);
        }

        @Override
        public String object2JSON(Object o) {
            return JSON.toJSONString(o);
        }
    }
}
