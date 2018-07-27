package com.spearbothy.router.api.entity;

import android.os.Bundle;

import java.util.HashMap;
import java.util.Map;

/**
 * @author mahao
 * @date 2018/7/27 上午9:51
 * @email zziamahao@163.com
 */

public class ResponseResult {

    // 路径信息
    private RouteAddition addition;

    // 跳转的参数
    private Bundle bundle;

    // 扩展参数
    private Map<String, Object> tags = new HashMap<>();

    public ResponseResult(RouteAddition addition) {
        this.addition = addition;
    }

    public RouteAddition getAddition() {
        return addition;
    }

    public void setAddition(RouteAddition addition) {
        this.addition = addition;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public void addTag(String key, Object object) {
        tags.put(key, object);
    }

    @Override
    public String toString() {
        return "ResponseResult{" +
                "addition=" + addition +
                ", bundle=" + bundle +
                ", tags=" + tags +
                '}';
    }
}
