package com.spearbothy.router.api.interceptor;

import com.spearbothy.router.api.router.Response;
import com.spearbothy.router.api.router.RouterRequest;

/**
 *
 * 责任链模式，在查找路由过程中添加自定义控制
 *
 * @author mahao
 * @date 2018/7/10 下午3:10
 * @email zziamahao@163.com
 */

public interface Interceptor {

    Response intercept(Chain chain);

    interface Chain {
        RouterRequest request();

        Response proceed(RouterRequest request);
    }
}
