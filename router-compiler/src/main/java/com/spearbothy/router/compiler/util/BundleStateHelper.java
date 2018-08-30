package com.spearbothy.router.compiler.util;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;

public class BundleStateHelper {

    public static MethodSpec.Builder statementSaveValueIntoBundle(MethodSpec.Builder methodBuilder, String fieldName, String fieldType, String instance, String bundleName) {
        String statement = null;
        switch (fieldType) {
            case "int":
                statement = String.format("%s.putInt(%s, %s)", bundleName, "\"" + fieldName + "\"", getStatement(instance, fieldName));
                break;
            case "long":
                statement = String.format("%s.putLong(%s, %s)", bundleName, "\"" + fieldName + "\"", getStatement(instance, fieldName));
                break;
            case "char":
                statement = String.format("%s.putChar(%s, %s)", bundleName, "\"" + fieldName + "\"", getStatement(instance, fieldName));
                break;
            case "short":
                statement = String.format("%s.putShort(%s, %s)", bundleName, "\"" + fieldName + "\"", getStatement(instance, fieldName));
                break;
            case "byte":
                statement = String.format("%s.putByte(%s, %s)", bundleName, "\"" + fieldName + "\"", getStatement(instance, fieldName));
                break;
            case "float":
                statement = String.format("%s.putFloat(%s, %s)", bundleName, "\"" + fieldName + "\"", getStatement(instance, fieldName));
                break;
            case "double":
                statement = String.format("%s.putDouble(%s, %s)", bundleName, "\"" + fieldName + "\"", getStatement(instance, fieldName));
                break;
            case "boolean":
                statement = String.format("%s.putBoolean(%s, %s)", bundleName, "\"" + fieldName + "\"", getStatement(instance, fieldName));
                break;
            case "java.lang.String":
                statement = String.format("%s.putString(%s, %s)", bundleName, "\"" + fieldName + "\"", getStatement(instance, fieldName));
                break;
            default:
                statement = String.format(
                        "%s.putString(%s, %s)", bundleName, "\"" + fieldName + "\"", "$T.getAutowiredJsonAdapter().object2JSON(" + getStatement(instance, fieldName) + ")"
                );
                methodBuilder.addStatement(statement, Constants.API_ROUTER);
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
                statement = assignStatement(instance, fieldName, String.format("%s.getInt(%s)", bundleName, "\"" + fieldName + "\""));
                break;
            case "long":
                statement = assignStatement(instance, fieldName, String.format("%s.getLong(%s)", bundleName, "\"" + fieldName + "\""));
                break;
            case "char":
                statement = assignStatement(instance, fieldName, String.format("%s.getChar(%s)", bundleName, "\"" + fieldName + "\""));
                break;
            case "short":
                statement = assignStatement(instance, fieldName, String.format("%s.getShort(%s)", bundleName, "\"" + fieldName + "\""));
                break;
            case "byte":
                statement = assignStatement(instance, fieldName, String.format("%s.getByte(%s)", bundleName, "\"" + fieldName + "\""));
                break;
            case "float":
                statement = assignStatement(instance, fieldName, String.format("%s.getFloat(%s)", bundleName, "\"" + fieldName + "\""));
                break;
            case "double":
                statement = assignStatement(instance, fieldName, String.format("%s.getDouble(%s)", bundleName, "\"" + fieldName + "\""));
                break;
            case "boolean":
                statement = assignStatement(instance, fieldName, String.format("%s.getBoolean(%s)", bundleName, "\"" + fieldName + "\""));
                break;
            case "java.lang.String":
                statement = assignStatement(instance, fieldName, String.format("%s.getString(%s)", bundleName, "\"" + fieldName + "\""));
                break;
            default:
                statement = assignStatement(instance, fieldName, String.format(
                        "$T.getAutowiredJsonAdapter().JSON2Object(%s.getString(%s), $T.class)",
                        bundleName,
                        "\"" + fieldName + "\""
                ));
                methodBuilder.addStatement(statement, Constants.API_ROUTER, ClassName.bestGuess(fieldType));
                statement = null;

        }
        if (statement != null) {
            methodBuilder.addStatement(statement);
        }
        return methodBuilder;
    }

    private static String assignStatement(String instance, String field, String value) {
        return String.format("%s.%s = %s", instance, field, value);
    }

    private static String getStatement(String instance, String field) {
        return String.format("%s.%s", instance, field);
    }
}
