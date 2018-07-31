package com.spearbothy.router.sample.interceptor;

import android.content.Context;
import android.widget.Toast;

import com.spearbothy.router.App;
import com.spearbothy.router.sample.MainActivity;
import com.spearbothy.router.api.ResultCallback;
import com.spearbothy.router.api.Router;
import com.spearbothy.router.api.interceptor.Interceptor;
import com.spearbothy.router.api.router.Response;
import com.spearbothy.router.api.router.RouterRequest;

import java.util.Map;

/**
 * @author mahao
 * @date 2018/7/27 下午4:43
 * @email zziamahao@163.com
 */

public class LoginInterceptor implements Interceptor {

    private static final int CODE_FAIL_NO_LOGIN = -2001;

    @Override
    public Response intercept(Chain chain) {
        RouterRequest request = chain.request();
        // 获取自定义参数
        Map<String, String> params = request.getParams();
        // 获取登录自动
        String needLogin = params.get("needLogin");
        if ("true".equals(needLogin)) {
            // 如果需要登录，但当前未登录，则跳转登录页面
            if (!App.sIsLogin) {
                // 启动新的路由
                gotoLogin(request.getContext());

                Response response = new Response();
                response.setError(CODE_FAIL_NO_LOGIN, "请先登录");
                return response;
            }
        }
        return chain.proceed(request);
    }

    private void gotoLogin(Context context) {
        Router.with(context)
                .url("router://app/login?version=1.0")
                .start(new ResultCallback() {
                    @Override
                    public void onSuccess() {}

                    @Override
                    public void onError(Response response) {
                        Toast.makeText(context, response.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
