package com.spearbothy.router.compiler.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mahao
 * @date 2018/7/16 下午5:40
 * @email zziamahao@163.com
 */

public class RouterDetail {

    private String name;

    private List<Path> pathList = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Path> getPathList() {
        return pathList;
    }

    public void setPathList(List<Path> pathList) {
        this.pathList = pathList;
    }

    public void addPath(Path entity) {
        pathList.add(entity);
    }

    public static class Path {

        private String className;

        private String path;

        private Map<String, Object> params = new HashMap<>();

        private String desc;

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public Path(String className) {
            this.className = className;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path, String moduleName, String protocol) {
            this.path = protocol + "://" + moduleName + path;
        }

        public Map<String, Object> getParams() {
            return params;
        }

        public Path addParams(String key, Object value) {
            params.put(key, value);
            return this;
        }
    }
}
