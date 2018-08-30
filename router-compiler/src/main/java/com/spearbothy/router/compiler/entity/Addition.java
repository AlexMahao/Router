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
    /**
     * 注解所在的全路径类名
     */
    private String qualifiedName;
    /**
     * 被@Route注解的类以及相关信息
     */
    private RouteClass routeClass;
    /**
     * 被@Autowired注解的字段的相关信息
     */
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

    @Override
    public String toString() {
        return "Addition{" +
                "qualifiedName='" + qualifiedName + '\'' +
                ", routeClass=" + routeClass +
                ", autowiredFields=" + autowiredFields +
                '}';
    }
}
