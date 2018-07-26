package com.spearbothy.router.api.router;

import com.spearbothy.router.api.Router;
import com.spearbothy.router.api.entity.ModuleEntity;
import com.spearbothy.router.api.entity.RouteAddition;
import com.spearbothy.router.api.interceptor.Interceptor;
import com.spearbothy.router.api.util.Logger;

import java.util.ArrayList;

/**
 * @author mahao
 * @date 2018/6/27 下午3:19
 * @email zziamahao@163.com
 */

public class Warehouse {

    private ArrayList<ModuleEntity> routerModuleList = new ArrayList<>();

    // 拦截器
    private ArrayList<Interceptor> interceptors = new ArrayList<>();

//    private Comparator<Interceptor> interceptorComparator = new InterceptorComparator();

    public void addModule(ModuleEntity moduleEntity, boolean force) {
        int index = routerModuleList.indexOf(moduleEntity);
        if (index >= 0) {
            if (Router.DEBUG) {
                ModuleEntity moduleHistory = routerModuleList.get(index);
                Logger.error("已存在对应module,信息如下");
                Logger.error(moduleHistory.toString());
            }
            // 对应module已经存在
            if (force) {
                routerModuleList.remove(index);
                routerModuleList.add(moduleEntity);
                if (Router.DEBUG) {
                    Logger.error("替换module,新module如下");
                    Logger.error(moduleEntity.toString());
                }
            }
        } else {
            routerModuleList.add(moduleEntity);
        }
    }

    public void addModule(ModuleEntity moduleEntity) {
        addModule(moduleEntity, true);
    }


    public ModuleEntity findModule(String host) {
        int index = routerModuleList.indexOf(new ModuleEntity(host));
        if (index >= 0) {
            return routerModuleList.get(index);
        }
        return null;
    }

    public RouteAddition findRoute(ModuleEntity module, String path) {
        if (module == null) {
            return null;
        }
        return module.getRouteMap().get(path);
    }

    public void addInterceptor(Interceptor interceptor) {
        interceptors.add(interceptor);
        // 排序
//        Collections.sort(interceptors, interceptorComparator);
    }

    public ArrayList<Interceptor> getInterceptor() {
        return interceptors;
    }
}
