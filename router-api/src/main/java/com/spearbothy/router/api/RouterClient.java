package com.spearbothy.router.api;

import android.content.Context;

import java.net.URL;

/**
 * 功能分发
 * Created by android-dev on 2018/3/28.
 */
public class RouterClient {

    static String ROUTER_PROTOCOL = "router";

    public static RouterRequest with(Context context) {
        return new RouterRequest(context);
    }

    public static void init(String moduleName) {
        // 支持自定义协议解析
        URL.setURLStreamHandlerFactory(new RouterURLStreamHandlerFactory());
        RouterManager.add(moduleName);
    }

    public static void addModule(String moduleName) {
        RouterManager.add(moduleName);
    }
}
