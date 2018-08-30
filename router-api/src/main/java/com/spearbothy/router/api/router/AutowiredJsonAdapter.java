package com.spearbothy.router.api.router;

/**
 * 路由协议中实现自定义解析，不强制依赖fastJson
 *
 * @author mahao
 * @date 2018/8/17 上午11:25
 */
public interface AutowiredJsonAdapter {

    <T> T JSON2Object(String json, Class<T> clazz);

    String object2JSON(Object object);
}
