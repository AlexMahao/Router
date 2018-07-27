package com.spearbothy.router.compiler.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.squareup.javapoet.MethodSpec;

import javax.lang.model.element.Element;

public class BundleStateHelper {

    public static MethodSpec.Builder statementSaveValueIntoBundle(MethodSpec.Builder methodBuilder,String fieldName, String fieldType, String instance, String bundleName) {
        String statement = null;
        switch (fieldType) {
            case "int":
                statement = String.format("%s.putInt(%s, %s)", bundleName, "\"" + fieldName + "\"", getStatement(instance, fieldName));
                break;
            case "long":
                statement = String.format("%s.putLong(%s, %s)", bundleName, "\"" + fieldName + "\"",
                        getStatement(instance, fieldName));
                break;
            case "char":
                statement = String.format("%s.putChar(%s, %s)", bundleName, "\"" + fieldName + "\"",
                        getStatement(instance, fieldName));
                break;
            case "short":
                statement = String.format("%s.putShort(%s, %s)", bundleName, "\"" + fieldName + "\"",
                        getStatement(instance, fieldName));
                break;
            case "byte":
                statement = String.format("%s.putByte(%s, %s)", bundleName, "\"" + fieldName + "\"",
                        getStatement(instance, fieldName));
                break;
            case "float":
                statement = String.format("%s.putFloat(%s, %s)", bundleName, "\"" + fieldName + "\"",
                        getStatement(instance, fieldName));
                break;
            case "double":
                statement = String.format("%s.putDouble(%s, %s)", bundleName, "\"" + fieldName + "\"",
                        getStatement(instance, fieldName));
                break;
            case "boolean":
                statement = String.format("%s.putBoolean(%s, %s)", bundleName, "\"" + fieldName + "\"",
                        getStatement(instance, fieldName));
                break;
            default:
                statement = String.format(
                        "%s.putString(%s, %s)", bundleName, "\"" + fieldName + "\"", "$T.toJSONString(" + getStatement(instance, fieldName) + ")"
                );
                methodBuilder.addStatement(statement, JSON.class);
                statement = null;
        }
        if (statement != null) {
            methodBuilder.addStatement(statement);
        }
        return methodBuilder;
    }

    public static MethodSpec.Builder statementGetValueFromBundle(MethodSpec.Builder methodBuilder, String fieldName, String fieldType, String instance, String bundleName) {
        String statement = null;

        switch (fieldType) {
            case "int":
                statement = assignStatement(instance, fieldName,
                        String.format("%s.getInt(%s)", bundleName, "\"" + fieldName + "\""));
                break;
            case "long":
                statement = assignStatement(instance, fieldName,
                        String.format("%s.getLong(%s)", bundleName, "\"" + fieldName + "\""));
                break;
            case "char":
                statement = assignStatement(instance, fieldName,
                        String.format("%s.getChar(%s)", bundleName, "\"" + fieldName + "\""));
                break;
            case "short":
                statement = assignStatement(instance, fieldName,
                        String.format("%s.getShort(%s)", bundleName, "\"" + fieldName + "\""));
                break;
            case "byte":
                statement = assignStatement(instance, fieldName,
                        String.format("%s.getByte(%s)", bundleName, "\"" + fieldName + "\""));
                break;
            case "float":
                statement = assignStatement(instance, fieldName,
                        String.format("%s.getFloat(%s)", bundleName, "\"" + fieldName + "\""));
                break;
            case "double":
                statement = assignStatement(instance, fieldName,
                        String.format("%s.getDouble(%s)", bundleName, "\"" + fieldName + "\""));
                break;
            case "boolean":
                statement = assignStatement(instance, fieldName,
                        String.format("%s.getBoolean(%s)", bundleName, "\"" + fieldName + "\""));
                break;
            default:
                statement = assignStatement(instance, fieldName, String.format(
                        "$T.<%s>parseObject(%s.getString(%s), new $T<%s>(){}.getType())",
                        fieldType,
                        bundleName,
                        "\"" + fieldName + "\"",
                        fieldType
                ));
                methodBuilder.addStatement(statement, JSON.class, TypeReference.class);
                statement = null;

        }
        if (statement != null) {
            methodBuilder.addStatement(statement);
        }
        return methodBuilder;
    }

    private static String javaAssignStatement(String instance, String field, String value) {
        return String.format("%s.%s = %s", instance, field, value);
    }

    private static String assignStatement(String instance, String field, String value) {
        return javaAssignStatement(instance, field, value);
    }

    private static String javaGetStatement(String instance, String field) {
        return String.format("%s.%s", instance, field);
    }


    private static String getStatement(String instance, String field) {
        return javaGetStatement(instance, field);
    }
}
