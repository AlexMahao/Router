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
            case "java.lang.Integer":
            case "java.lang.Long":
            case "java.lang.Character":
            case "java.lang.Short":
            case "java.lang.Byte":
            case "java.lang.Float":
            case "java.lang.Double":
            case "java.lang.Boolean":
                statement = String.format("%s.putSerializable(%s, %s)", bundleName, "\"" + fieldName + "\"",
                        getStatement(instance, fieldName));
                break;
            case "java.lang.String":
                statement = String.format("%s.putString(%s, %s)", bundleName, "\"" + fieldName + "\"",
                        getStatement(instance, fieldName));
                break;
            case "java.io.Serializable":
                statement = String.format("%s.putSerializable(%s, %s)", bundleName, "\"" + fieldName + "\"",
                        getStatement(instance, fieldName));
                break;
            case "android.os.IBinder":
                statement = String.format("%s.putBinder(%s, %s)", bundleName, "\"" + fieldName + "\"",
                        getStatement(instance, fieldName));
                break;
            case "android.os.Bundle":
                statement = String.format("%s.putBundle(%s, %s)", bundleName, "\"" + fieldName + "\"",
                        getStatement(instance, fieldName));
                break;
            case "java.lang.CharSequence":
                statement = String.format("%s.putCharSequence(%s, %s)", bundleName, "\"" + fieldName + "\"",
                        getStatement(instance, fieldName));
                break;
            case "android.os.Parcelable":
                statement = String.format("%s.putParcelable(%s, %s)", bundleName, "\"" + fieldName + "\"",
                        getStatement(instance, fieldName));
                break;
            case "android.util.Size":
                statement = String.format("%s.putSize(%s, %s)", bundleName, "\"" + fieldName + "\"",
                        getStatement(instance, fieldName));
                break;
            case "android.util.SizeF":
                statement = String.format("%s.putSizeF(%s, %s)", bundleName, "\"" + fieldName + "\"",
                        getStatement(instance, fieldName));
                break;
            case "android.os.Parcelable[]":
                statement = String.format("%s.putParcelableArray(%s, %s)", bundleName, "\"" + fieldName + "\"",
                        getStatement(instance, fieldName));
                break;
            case "byte[]":
                statement = String.format("%s.putByteArray(%s, %s)", bundleName, "\"" + fieldName + "\"",
                        getStatement(instance, fieldName));
                break;
            case "short[]":
                statement = String.format("%s.putShortArray(%s, %s)", bundleName, "\"" + fieldName + "\"",
                        getStatement(instance, fieldName));
                break;
            case "char[]":
                statement = String.format("%s.putCharArray(%s, %s)", bundleName, "\"" + fieldName + "\"",
                        getStatement(instance, fieldName));
                break;
            case "float[]":
                statement = String.format("%s.putFloatArray(%s, %s)", bundleName, "\"" + fieldName + "\"",
                        getStatement(instance, fieldName));
                break;
            case "java.lang.CharSequence[]":
                statement = String.format("%s.putCharSequenceArray(%s, %s)", bundleName, "\"" + fieldName + "\"",
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
            case "java.lang.Integer":
            case "java.lang.Long":
            case "java.lang.Character":
            case "java.lang.Short":
            case "java.lang.Byte":
            case "java.lang.Float":
            case "java.lang.Double":
            case "java.lang.Boolean":
                statement = assignStatement(instance, fieldName,
                        String.format(
                                "(%s)%s.getSerializable(%s)",
                                fieldType,
                                bundleName, "\"" + fieldName + "\""
                        )
                );
                break;
            case "java.lang.String":
                statement = assignStatement(instance, fieldName,
                        String.format("%s.getString(%s)", bundleName, "\"" + fieldName + "\""));
                break;
            case "java.io.Serializable":
                statement = assignStatement(instance, fieldName,
                        String.format("%s.getSerializable(%s)", bundleName, "\"" + fieldName + "\""));
                break;
            case "android.os.IBinder":
                statement = assignStatement(instance, fieldName,
                        String.format("%s.getBinder(%s)", bundleName, "\"" + fieldName + "\""));
                break;
            case "android.os.Bundle":
                statement = assignStatement(instance, fieldName,
                        String.format("%s.getBundle(%s)", bundleName, "\"" + fieldName + "\""));
                break;
            case "java.lang.CharSequence":
                statement = assignStatement(instance, fieldName,
                        String.format("%s.getCharSequence(%s)", bundleName, "\"" + fieldName + "\""));
                break;
            case "android.os.Parcelable":
                statement = assignStatement(instance, fieldName,
                        String.format("%s.getParcelable(%s)", bundleName, "\"" + fieldName + "\""));
                break;
            case "android.util.Size":
                statement = assignStatement(instance, fieldName,
                        String.format("%s.getSize(%s)", bundleName, "\"" + fieldName + "\""));
                break;
            case "android.util.SizeF":
                statement = assignStatement(instance, fieldName,
                        String.format("%s.getSizeF(%s)", bundleName, "\"" + fieldName + "\""));
                break;
            case "android.os.Parcelable[]":
                statement = assignStatement(instance, fieldName,
                        String.format("%s.getParcelableArray(%s)", bundleName, "\"" + fieldName + "\""));
                break;
            case "byte[]":
                statement = assignStatement(instance, fieldName,
                        String.format("%s.getByteArray(%s)", bundleName, "\"" + fieldName + "\""));
                break;
            case "short[]":
                statement = assignStatement(instance, fieldName,
                        String.format("%s.getShortArray(%s)", bundleName, "\"" + fieldName + "\""));
                break;
            case "char[]":
                statement = assignStatement(instance, fieldName,
                        String.format("%s.getCharArray(%s)", bundleName, "\"" + fieldName + "\""));
                break;
            case "float[]":
                statement = assignStatement(instance, fieldName,
                        String.format("%s.getFloatArray(%s)", bundleName, "\"" + fieldName + "\""));
                break;
            case "java.lang.CharSequence[]":
                statement = assignStatement(instance, fieldName,
                        String.format("%s.getCharSequenceArray(%s)", bundleName, "\"" + fieldName + "\""));
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
