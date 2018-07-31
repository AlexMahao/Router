package com.spearbothy.router.api;

import android.content.Context;

import com.spearbothy.router.api.interceptor.Interceptor;
import com.spearbothy.router.api.interceptor.VerifyParamsInterceptor;
import com.spearbothy.router.api.router.RouterClient;
import com.spearbothy.router.api.router.RouterRequest;

/**
 * @author mahao
 * @date 2018/6/27 上午11:57
 * @email zziamahao@163.com
 */

public class Router {

    public static boolean DEBUG = false;

    public static void init(Context context) {
        RouterClient.init(context);
        addInterceptor(new VerifyParamsInterceptor());
    }

    public static RouterRequest with(Context context) {
        return new RouterRequest(context);
    }

    public static void addInterceptor(Interceptor interceptor) {
        RouterClient.getInstance().warehouse.addInterceptor(interceptor);
    }

    public static void setDebug(boolean debug) {
        DEBUG = debug;
    }
}
