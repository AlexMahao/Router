package com.spearbothy.router.entity;

/**
 * Created by android-dev on 2018/6/26.
 */

public class RouteEntity {

    private Class clazz;

    private String desc;

    public RouteEntity(Class clazz, String desc) {
        this.clazz = clazz;
        this.desc = desc;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "RouteEntity{" +
                "clazz=" + clazz +
                ", desc='" + desc + '\'' +
                '}';
    }
}
