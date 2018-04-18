package com.spearbothy.router.api;

import com.spearbothy.router.RouterEntity;

import java.util.Map;

/**
 * Created by android-dev on 2018/3/28.
 */

public interface IRouterLoader {
    // 数据初始化操作
    void init(Map<String, RouterEntity> map);

    //  map.put("path" , new RouterMeta(xxxx.class,desc))
}
