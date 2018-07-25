package com.spearbothy.router.api.entity;

/**
 * @author mahao
 * @date 2018/7/25 下午3:14
 * @email zziamahao@163.com
 */

public class AutowiredField {

    private String fieldName;

    private String fieldType;

    public AutowiredField(String fieldName, String fieldType) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
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
}