package com.spearbothy.router.api.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author mahao
 * @date 2018/6/27 下午3:29
 * @email zziamahao@163.com
 */

public class ModuleEntity {
    private String moduleName;

    private Map<String, RouteAddition> routeMap = new HashMap<>();

    public ModuleEntity(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public Map<String, RouteAddition> getRouteMap() {
        return routeMap;
    }

    public void setRouteMap(Map<String, RouteAddition> routeMap) {
        this.routeMap = routeMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModuleEntity that = (ModuleEntity) o;

        return moduleName != null ? moduleName.equals(that.moduleName) : that.moduleName == null;
    }

    @Override
    public int hashCode() {
        return moduleName != null ? moduleName.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ModuleEntity{" +
                "moduleName='" + moduleName + '\'' +
                ", routeMap=" + routeMap +
                '}';
    }
}
