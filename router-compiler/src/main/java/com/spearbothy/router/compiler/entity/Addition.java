package com.spearbothy.router.compiler.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 生成代码的所有信息
 *
 * @author mahao
 * @date 2018/7/25 下午3:02
 * @email zziamahao@163.com
 */
public class Addition {

    // 全路径名
    private String qualifiedName;

    // 被route注解的类信息
    private RouteClass routeClass;

    // @autowird 注解的所有参数
    private List<AutowiredField> autowiredFields = new ArrayList<>();


    public String getPackageName() {
        if (qualifiedName == null || "".equals(qualifiedName)) {
            return "";
        }
        return qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
    }

    public String getSimpleName() {
        if (qualifiedName == null || "".equals(qualifiedName)) {
            return "";
        }
        return qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1, qualifiedName.length());
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public RouteClass getRouteClass() {
        return routeClass;
    }

    public void setRouteClass(RouteClass routeClass) {
        this.routeClass = routeClass;
    }

    public List<AutowiredField> getAutowiredFields() {
        return autowiredFields;
    }

    public void addAutowiredField(AutowiredField autowiredField) {
        autowiredFields.add(autowiredField);
    }
}
