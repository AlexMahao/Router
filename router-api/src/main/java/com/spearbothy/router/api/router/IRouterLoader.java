package com.spearbothy.router.api.router;

import com.spearbothy.router.api.entity.RouteAddition;

import java.util.Map;

/**
 * Created by android-dev on 2018/3/28.
 */

public interface IRouterLoader {
    // 加载module
    void loadInto(Map<String, RouteAddition> root);

    // 获取module的名字
    String getModuleName();
}
