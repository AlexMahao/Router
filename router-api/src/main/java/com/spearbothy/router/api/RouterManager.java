package com.spearbothy.router.api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.spearbothy.router.RouterEntity;

import java.util.TreeMap;

/**
 * Created by android-dev on 2018/3/28.
 */

class RouterManager {
    private static final String TAG = RouterManager.class.getSimpleName();

    private static TreeMap<String, RouterHandler> sRouter = new TreeMap<>();

    static void add(String packageName) {
        try {
            RouterHandler handler = new RouterHandler();
            String className = packageName + ".RouterLoaderImpl";
            IRouterLoader loader = (IRouterLoader) Class.forName(className).newInstance();
            loader.init(handler.getMap());
            handler.setPackageName(packageName);
            sRouter.put(packageName, handler);
        } catch (Exception e) {
            RouterLog.error("RouterClient 初始化错误:" + packageName, e);
        }
    }

    static void process(RouterRequest request) {
        // 数据校验并查找对应处理的RouterHandler
        if (request.getContext() instanceof Activity) {
            if (RouterClient.ROUTER_PROTOCOL.equals(request.getProtocol())) {
                RouterHandler routerHandler = sRouter.get(request.getHost());
                if (routerHandler != null) {
                    RouterEntity meta = routerHandler.findMeta(request.getPath());
                    if (meta != null) {
                        startActivity(request, meta);
                    } else {
                        error(request, RouterResponse.ERROR_PROTOCOL, "路径不匹配");
                    }
                } else {
                    error(request, RouterResponse.ERROR_PROTOCOL, "模块未找到");
                }
            } else {
                error(request, RouterResponse.ERROR_PROTOCOL, "协议不合法：" + request.getProtocol());
            }
        }
    }

    private static void error(RouterRequest request, int code, String msg) {
        request.getCallback().onError(new RouterResponse(code, msg));
    }


    private static void startActivity(RouterRequest request, RouterEntity meta) {
        Context context = request.getContext();
        Intent intent = new Intent(context, meta.getClazz());
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }
}
