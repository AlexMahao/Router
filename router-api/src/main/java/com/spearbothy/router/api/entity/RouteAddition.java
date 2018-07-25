package com.spearbothy.router.api.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Route附加信息
 *
 * @author mahao
 * @date 2018/7/25 下午4:20
 * @email zziamahao@163.com
 */

public class RouteAddition {
    private Class clazz;

    private String desc;

    private String version;

    public RouteAddition(Class clazz, String desc, String version) {
        this.clazz = clazz;
        this.desc = desc;
        this.version = version;
    }

    private List<AutowiredField> autowiredFields = new ArrayList<>();

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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<AutowiredField> getAutowiredFields() {
        return autowiredFields;
    }

    public void setAutowiredFields(List<AutowiredField> autowiredFields) {
        this.autowiredFields = autowiredFields;
    }

    public void addAutowiredField(String fieldName, String fieldType) {
        autowiredFields.add(new AutowiredField(fieldName, fieldType));
    }
}
