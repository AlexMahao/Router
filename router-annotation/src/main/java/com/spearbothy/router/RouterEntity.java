package com.spearbothy.router;

/**
 * Created by android-dev on 2018/4/3.
 */

public class RouterEntity {
    private Class clazz;
    private String desc;

    public RouterEntity(Class clazz, String desc) {
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
}
