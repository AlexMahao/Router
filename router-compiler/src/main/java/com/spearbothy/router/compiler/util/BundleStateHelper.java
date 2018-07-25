package com.spearbothy.router.compiler.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.squareup.javapoet.MethodSpec;

import javax.lang.model.element.Element;

public class BundleStateHelper {

    public static MethodSpec.Builder statementSaveValueIntoBundle(MethodSpec.Builder methodBuilder, Element element, String instance, String bundleName) {
        String statement = null;
        String varName = element.getSimpleName().toString();

        switch (element.asType().toString()) {
            case "int":
                statement = String.format("%s.putInt(%s, %s)", bundleName, "\"" + varName + "\"", getStatement(instance, varName));
                break;
            case "long":
                statement = String.format("%s.putLong(%s, %s)", bundleName, "\"" + varName + "\"",
                        getStatement(instance, varName));
                break;
            case "char":
                statement = String.format("%s.putChar(%s, %s)", bundleName, "\"" + varName + "\"",
                        getStatement(instance, varName));
                break;
            case "short":
                statement = String.format("%s.putShort(%s, %s)", bundleName, "\"" + varName + "\"",
                        getStatement(instance, varName));
                break;
            case "byte":
                statement = String.format("%s.putByte(%s, %s)", bundleName, "\"" + varName + "\"",
                        getStatement(instance, varName));
                break;
            case "float":
                statement = String.format("%s.putFloat(%s, %s)", bundleName, "\"" + varName + "\"",
                        getStatement(instance, varName));
                break;
            case "double":
                statement = String.format("%s.putDouble(%s, %s)", bundleName, "\"" + varName + "\"",
                        getStatement(instance, varName));
                break;
            case "boolean":
                statement = String.format("%s.putBoolean(%s, %s)", bundleName, "\"" + varName + "\"",
                        getStatement(instance, varName));
                break;
            case "java.lang.Integer":
            case "java.lang.Long":
            case "java.lang.Character":
            case "java.lang.Short":
            case "java.lang.Byte":
            case "java.lang.Float":
            case "java.lang.Double":
            case "java.lang.Boolean":
                statement = String.format("%s.putSerializable(%s, %s)", bundleName, "\"" + varName + "\"",
                        getStatement(instance, varName));
                break;
            case "java.lang.String":
                statement = String.format("%s.putString(%s, %s)", bundleName, "\"" + varName + "\"",
                        getStatement(instance, varName));
                break;
            case "java.io.Serializable":
                statement = String.format("%s.putSerializable(%s, %s)", bundleName, "\"" + varName + "\"",
                        getStatement(instance, varName));
                break;
            case "android.os.IBinder":
                statement = String.format("%s.putBinder(%s, %s)", bundleName, "\"" + varName + "\"",
                        getStatement(instance, varName));
                break;
            case "android.os.Bundle":
                statement = String.format("%s.putBundle(%s, %s)", bundleName, "\"" + varName + "\"",
                        getStatement(instance, varName));
                break;
            case "java.lang.CharSequence":
                statement = String.format("%s.putCharSequence(%s, %s)", bundleName, "\"" + varName + "\"",
                        getStatement(instance, varName));
                break;
            case "android.os.Parcelable":
                statement = String.format("%s.putParcelable(%s, %s)", bundleName, "\"" + varName + "\"",
                        getStatement(instance, varName));
                break;
            case "android.util.Size":
                statement = String.format("%s.putSize(%s, %s)", bundleName, "\"" + varName + "\"",
                        getStatement(instance, varName));
                break;
            case "android.util.SizeF":
                statement = String.format("%s.putSizeF(%s, %s)", bundleName, "\"" + varName + "\"",
                        getStatement(instance, varName));
                break;
            case "android.os.Parcelable[]":
                statement = String.format("%s.putParcelableArray(%s, %s)", bundleName, "\"" + varName + "\"",
                        getStatement(instance, varName));
                break;
            case "byte[]":
                statement = String.format("%s.putByteArray(%s, %s)", bundleName, "\"" + varName + "\"",
                        getStatement(instance, varName));
                break;
            case "short[]":
                statement = String.format("%s.putShortArray(%s, %s)", bundleName, "\"" + varName + "\"",
                        getStatement(instance, varName));
                break;
            case "char[]":
                statement = String.format("%s.putCharArray(%s, %s)", bundleName, "\"" + varName + "\"",
                        getStatement(instance, varName));
                break;
            case "float[]":
                statement = String.format("%s.putFloatArray(%s, %s)", bundleName, "\"" + varName + "\"",
                        getStatement(instance, varName));
                break;
            case "java.lang.CharSequence[]":
                statement = String.format("%s.putCharSequenceArray(%s, %s)", bundleName, "\"" + varName + "\"",
                        getStatement(instance, varName));
                break;
            default:
                statement = String.format(
                        "%s.putString(%s, %s)", bundleName, "\"" + varName + "\"", "$T.toJSONString(" + getStatement(instance, varName) + ")"
                );
                methodBuilder.addStatement(statement, JSON.class);
                statement = null;
        }
        if (statement != null) {
            methodBuilder.addStatement(statement);
        }
        return methodBuilder;
    }

    public static MethodSpec.Builder statementGetValueFromBundle(MethodSpec.Builder methodBuilder, Element element, String instance, String bundleName) {
        String statement = null;
        String varName = element.getSimpleName().toString();

        switch (element.asType().toString()) {
            case "int":
                statement = assignStatement(instance, varName,
                        String.format("%s.getInt(%s)", bundleName, "\"" + varName + "\""));
                break;
            case "long":
                statement = assignStatement(instance, varName,
                        String.format("%s.getLong(%s)", bundleName, "\"" + varName + "\""));
                break;
            case "char":
                statement = assignStatement(instance, varName,
                        String.format("%s.getChar(%s)", bundleName, "\"" + varName + "\""));
                break;
            case "short":
                statement = assignStatement(instance, varName,
                        String.format("%s.getShort(%s)", bundleName, "\"" + varName + "\""));
                break;
            case "byte":
                statement = assignStatement(instance, varName,
                        String.format("%s.getByte(%s)", bundleName, "\"" + varName + "\""));
                break;
            case "float":
                statement = assignStatement(instance, varName,
                        String.format("%s.getFloat(%s)", bundleName, "\"" + varName + "\""));
                break;
            case "double":
                statement = assignStatement(instance, varName,
                        String.format("%s.getDouble(%s)", bundleName, "\"" + varName + "\""));
                break;
            case "boolean":
                statement = assignStatement(instance, varName,
                        String.format("%s.getBoolean(%s)", bundleName, "\"" + varName + "\""));
                break;
            case "java.lang.Integer":
            case "java.lang.Long":
            case "java.lang.Character":
            case "java.lang.Short":
            case "java.lang.Byte":
            case "java.lang.Float":
            case "java.lang.Double":
            case "java.lang.Boolean":
                statement = assignStatement(instance, varName,
                        String.format(
                                "(%s)%s.getSerializable(%s)",
                                element.asType().toString(),
                                bundleName, "\"" + varName + "\""
                        )
                );
                break;
            case "java.lang.String":
                statement = assignStatement(instance, varName,
                        String.format("%s.getString(%s)", bundleName, "\"" + varName + "\""));
                break;
            case "java.io.Serializable":
                statement = assignStatement(instance, varName,
                        String.format("%s.getSerializable(%s)", bundleName, "\"" + varName + "\""));
                break;
            case "android.os.IBinder":
                statement = assignStatement(instance, varName,
                        String.format("%s.getBinder(%s)", bundleName, "\"" + varName + "\""));
                break;
            case "android.os.Bundle":
                statement = assignStatement(instance, varName,
                        String.format("%s.getBundle(%s)", bundleName, "\"" + varName + "\""));
                break;
            case "java.lang.CharSequence":
                statement = assignStatement(instance, varName,
                        String.format("%s.getCharSequence(%s)", bundleName, "\"" + varName + "\""));
                break;
            case "android.os.Parcelable":
                statement = assignStatement(instance, varName,
                        String.format("%s.getParcelable(%s)", bundleName, "\"" + varName + "\""));
                break;
            case "android.util.Size":
                statement = assignStatement(instance, varName,
                        String.format("%s.getSize(%s)", bundleName, "\"" + varName + "\""));
                break;
            case "android.util.SizeF":
                statement = assignStatement(instance, varName,
                        String.format("%s.getSizeF(%s)", bundleName, "\"" + varName + "\""));
                break;
            case "android.os.Parcelable[]":
                statement = assignStatement(instance, varName,
                        String.format("%s.getParcelableArray(%s)", bundleName, "\"" + varName + "\""));
                break;
            case "byte[]":
                statement = assignStatement(instance, varName,
                        String.format("%s.getByteArray(%s)", bundleName, "\"" + varName + "\""));
                break;
            case "short[]":
                statement = assignStatement(instance, varName,
                        String.format("%s.getShortArray(%s)", bundleName, "\"" + varName + "\""));
                break;
            case "char[]":
                statement = assignStatement(instance, varName,
                        String.format("%s.getCharArray(%s)", bundleName, "\"" + varName + "\""));
                break;
            case "float[]":
                statement = assignStatement(instance, varName,
                        String.format("%s.getFloatArray(%s)", bundleName, "\"" + varName + "\""));
                break;
            case "java.lang.CharSequence[]":
                statement = assignStatement(instance, varName,
                        String.format("%s.getCharSequenceArray(%s)", bundleName, "\"" + varName + "\""));
                break;
            default:

                statement = assignStatement(instance, varName, String.format(
                        "$T.<%s>parseObject(%s.getString(%s), new $T<%s>(){}.getType())",
                        element.asType().toString(),
                        bundleName,
                        "\"" + varName + "\"",
                        element.asType().toString()
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
