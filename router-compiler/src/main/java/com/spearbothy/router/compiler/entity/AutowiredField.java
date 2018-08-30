package com.spearbothy.router.compiler.entity;

/**
 * @author mahao
 * @date 2018/7/25 下午3:14
 * @email zziamahao@163.com
 */

public class AutowiredField {

    private String fieldName;

    private String fieldType;

    private String value;

    private String desc;

    private boolean enable;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "AutowiredField{" +
                "fieldName='" + fieldName + '\'' +
                ", fieldType='" + fieldType + '\'' +
                ", value='" + value + '\'' +
                ", desc='" + desc + '\'' +
                ", enable=" + enable +
                '}';
    }
}
