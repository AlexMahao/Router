package com.spearbothy.router.api;

import android.content.Context;

import com.spearbothy.router.api.router.RouterClient;
import com.spearbothy.router.api.router.RouterRequest;
import com.spearbothy.router.api.util.RouterURLStreamHandlerFactory;

import java.net.URL;

/**
 * @author mahao
 * @date 2018/6/27 上午11:57
 * @email zziamahao@163.com
 */

public class Router {

    public static boolean DEBUG = false;

    public static void init(Context context) {
        // 扩展url解析，自定义router://
        URL.setURLStreamHandlerFactory(new RouterURLStreamHandlerFactory());
        RouterClient.init(context);
    }

    public static RouterRequest with(Context context) {
        return new RouterRequest(context);
    }

    public static void setDebug(boolean debug) {
        DEBUG = debug;
    }
}
