package com.spearbothy.router.api;

import android.util.Log;

/**
 * Created by android-dev on 2018/3/28.
 */

public class RouterLog {
    private static final String TAG = RouterClient.class.getSimpleName();


    static void error(String msg, Throwable throwable) {
        Log.e(TAG, msg, throwable);
    }

    static void error(String msg) {
        Log.e(TAG, msg);
    }

    static void info(String msg) {
        Log.i(TAG, msg);
    }
}
