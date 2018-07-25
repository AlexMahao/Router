package com.spearbothy.router.api.interceptor;

import com.spearbothy.router.api.Constants;
import com.spearbothy.router.api.entity.ModuleEntity;
import com.spearbothy.router.api.entity.RouteAddition;
import com.spearbothy.router.api.router.Response;
import com.spearbothy.router.api.router.RouterClient;
import com.spearbothy.router.api.router.RouterRequest;
import com.spearbothy.router.entity.RouteEntity;

/**
 *
 * Interceptor 中可以添加对自定义参数的处理
 * @author mahao
 * @date 2018/7/10 下午3:24
 * @email zziamahao@163.com
 */

public class RouterInterceptorChain implements Interceptor.Chain {

    private int index;

    private RouterRequest request;

    public RouterInterceptorChain(int index, RouterRequest request) {
        this.index = index;
        this.request = request;
    }

    @Override
    public RouterRequest request() {
        return request;
    }

    @Override
    public Response proceed(RouterRequest request) {
        if (index < RouterClient.getInstance().warehouse.getInterceptor().size()) {
            Interceptor.Chain chain = new RouterInterceptorChain(index + 1, request);
            Interceptor interceptor = RouterClient.getInstance().warehouse.getInterceptor().get(index);
            return interceptor.intercept(chain);
        }
        return getResponse(request);
    }

    private static Response getResponse(RouterRequest request) {
        Response response = new Response();
        if (Constants.ROUTER_PROTOCOL.equals(request.getProtocol())) {
            // 查询对应routeEntity
            ModuleEntity module = RouterClient.getInstance().warehouse.findModule(request.getHost());
            if (module == null) {
                response.setError(Response.CODE_FAIL_ROUTER_NOT_FOUND, "模块未找到,请查看moduleName配置");
            } else {
                RouteAddition route = RouterClient.getInstance().warehouse.findRoute(module, request.getPath());
                if (route == null) {
                    response.setError(Response.CODE_FAIL_ROUTER_NOT_FOUND, "路径不匹配");
                } else {
                    response.setSuccess(route);
                }
            }
        } else {
            response.setError(Response.CODE_FAIL_PROTOCOL, "协议错误，协议应为router");
        }
        return response;
    }
}
