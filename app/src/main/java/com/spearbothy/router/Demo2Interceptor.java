package com.spearbothy.router;

import android.util.Log;

import com.spearbothy.router.api.interceptor.Interceptor;
import com.spearbothy.router.api.router.Response;

/**
 * @author mahao
 * @date 2018/7/16 上午11:24
 * @email zziamahao@163.com
 */

public class Demo2Interceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) {
        Log.i("Router", Demo2Interceptor.class.getSimpleName() + " interceptor start");
        Response proceed = chain.proceed(chain.request());
        Log.i("Router", Demo2Interceptor.class.getSimpleName() + " interceptor end");
        return proceed;
    }
}
