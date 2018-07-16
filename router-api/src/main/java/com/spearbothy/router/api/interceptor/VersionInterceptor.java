package com.spearbothy.router.api.interceptor;

import com.spearbothy.router.api.router.Response;

/**
 * @author mahao
 * @date 2018/7/10 下午3:22
 * @email zziamahao@163.com
 */

public class VersionInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) {
        // 模拟okHttp请求
        return chain.proceed(chain.request());
    }
}
