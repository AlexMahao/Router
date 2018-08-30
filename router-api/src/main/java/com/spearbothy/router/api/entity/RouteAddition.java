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
    // activity class
    private Class clazz;
    // 描述
    private String desc;
    // 版本
    private String version;
    // 注入的字段
    private List<AutowiredField> autowiredFields = new ArrayList<>();

    public RouteAddition(Class clazz, String desc, String version) {
        this.clazz = clazz;
        this.desc = desc;
        this.version = version;
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
        addAutowiredField(fieldName, fieldType, "", "", true);
    }

    public void addAutowiredField(String fieldName, String fieldType, String desc, String value, boolean enable) {
        AutowiredField autowiredField = new AutowiredField(fieldName, fieldType);
        autowiredField.setDesc(desc);
        autowiredField.setValue(value);
        autowiredFields.add(autowiredField);
    }

    @Override
    public String toString() {
        return "RouteAddition{" +
                "clazz=" + clazz +
                ", desc='" + desc + '\'' +
                ", version='" + version + '\'' +
                ", autowiredFields=" + autowiredFields +
                '}';
    }
}
