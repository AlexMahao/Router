package com.spearbothy.router.compiler.entity;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.VariableElement;

/**
 * @author mahao
 * @date 2018/7/24 下午2:12
 * @email zziamahao@163.com
 */

public class AutowiredDetail {

    private String qualifiedName;

    private List<VariableElement> elements = new ArrayList<>();

    public String getQualifiedName() {
        return qualifiedName;
    }

    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public List<VariableElement> getElements() {
        return elements;
    }

    public void addElements(VariableElement element) {
        elements.add(element);
    }

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

    @Override
    public String toString() {
        return "AutowiredDetail{" +
                "qualifiedName='" + qualifiedName + '\'' +
                ", elements=" + elements +
                '}';
    }
}
