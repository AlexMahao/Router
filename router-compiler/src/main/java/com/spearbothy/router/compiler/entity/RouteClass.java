package com.spearbothy.router.compiler.entity;

import com.squareup.javapoet.ClassName;

/**
 *
 * 被@Route注解的类信息
 * @author mahao
 * @date 2018/7/25 下午3:16
 * @email zziamahao@163.com
 */

public class RouteClass {

    private String path;

    private ClassName className;

    private String desc;

    private String version;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ClassName getClassName() {
        return className;
    }

    public void setClassName(ClassName className) {
        this.className = className;
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

    @Override
    public String toString() {
        return "RouteClass{" +
                "path='" + path + '\'' +
                ", className=" + className +
                ", desc='" + desc + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
