package com.spearbothy.router.api.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.spearbothy.router.api.Constants;
import com.spearbothy.router.api.entity.ModuleEntity;
import com.spearbothy.router.api.entity.ResponseResult;
import com.spearbothy.router.api.interceptor.RouterInterceptorChain;
import com.spearbothy.router.api.util.ClassUtils;
import com.spearbothy.router.api.util.Logger;

import java.util.Set;

/**
 * 路由map初始化，页面跳转
 *
 * @author mahao
 * @date 2018/6/27 下午12:08
 * @email zziamahao@163.com
 */

public class RouterClient {

    public Warehouse warehouse;

    private static RouterClient sClient = null;

    public static RouterClient getInstance() {
        return sClient;
    }

    /**
     * 加载路由图谱
     */
    public static synchronized void init(Context context) {
        if (sClient == null) {
            Logger.info("router init ...");
            // 初始化client，加载路由
            sClient = new RouterClient();
            sClient.warehouse = new Warehouse();
            try {
                Set<String> classList = ClassUtils.getFileNameByPackageName(context, Constants.ROUTER_LOADER_PACKAGE);
                // 获取所有类，加载方法
                for (String classPath : classList) {
                    Object instance = Class.forName(classPath).newInstance();
                    if (instance instanceof IRouterLoader) {
                        IRouterLoader loader = (IRouterLoader) instance;
                        ModuleEntity moduleEntity = new ModuleEntity(loader.getModuleName());
                        loader.loadInto(moduleEntity.getRouteMap());
                        sClient.warehouse.addModule(moduleEntity);
                    } else {
                        Logger.error("存在不属于Loader的类：" + classPath);
                    }
                }
                Logger.info("router init success !");
            } catch (Exception e) {
                Logger.error("router init fail !", e);
            }
        } else {
            Logger.error("router has init");
        }
    }

    static void process(RouterRequest request) {
        Response response = new RouterInterceptorChain(0, request).proceed(request);

        if (response == null) {
            return;
        }

        if (response.isSuccess()) {
            // 检查activity是否能跳转
            if (request.getCallback() != null) {
                request.getCallback().onSuccess();
            }
            startActivity(request, response.getResult());
        } else {
            if (request.getCallback() != null) {
                request.getCallback().onError(response);
            }
        }
    }

    private static void startActivity(RouterRequest request, ResponseResult result) {
        Context context = request.getContext();
        Intent intent = new Intent(context, result.getAddition().getClazz());
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra("router_bundle", result.getBundle());
        if (request.getActivityRequestCode() != 0 && context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent, request.getActivityRequestCode());
        } else {
            context.startActivity(intent);
        }

    }
}
