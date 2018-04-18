package com.spearbothy.router.api;

/**
 * Created by android-dev on 2018/3/28.
 */

import com.spearbothy.router.RouterEntity;

import java.util.TreeMap;

/**
 * 路由初始化和路由查找
 */
class RouterHandler {

    private String packageName;

    private TreeMap<String, RouterEntity> metaMap = new TreeMap<>();

    TreeMap<String, RouterEntity> getMap() {
        return metaMap;
    }

    void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    RouterEntity findMeta(String path) {
        return metaMap.get(path);
    }
}
