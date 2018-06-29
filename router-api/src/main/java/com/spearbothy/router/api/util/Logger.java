package com.spearbothy.router.api.util;

import android.util.Log;

import com.spearbothy.router.api.Router;

/**
 * @author mahao
 * @date 2018/6/27 下午3:25
 * @email zziamahao@163.com
 */

public class Logger {

    private static final String TAG = Router.class.getSimpleName();

    public static void error(String msg, Throwable throwable) {
        Log.e(TAG, msg, throwable);
    }

    public static void error(String msg) {
        Log.e(TAG, msg);
    }

    public static void info(String msg) {
        Log.i(TAG, msg);
    }
}
