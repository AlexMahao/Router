package com.spearbothy.router.api.interceptor;

import android.os.Bundle;
import android.text.TextUtils;

import com.spearbothy.router.api.entity.AutowiredField;
import com.spearbothy.router.api.router.Response;
import com.spearbothy.router.api.router.RouterRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 校验activity所需参数和url传入参数是否匹配
 *
 * @author mahao
 * @date 2018/7/26 上午10:38
 * @email zziamahao@163.com
 */

public class VerifyParamsInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) {
        RouterRequest request = chain.request();
        Response response = chain.proceed(request);
        if (response.isSuccess()) {
            Map<String, String> params = request.getParams();
            List<AutowiredField> autowiredFields = response.getResult().getAddition().getAutowiredFields();
            // 比较字段
            Bundle bundle = buildBundle(params, autowiredFields);
            if (bundle == null) {
                response.setError(Response.CODE_FAIL_PARAMS_NOT_VALID, "传入参数不合法");
            } else {
                response.getResult().setBundle(bundle);
            }
        }
        return response;
    }

    private Bundle buildBundle(Map<String, String> params, List<AutowiredField> autowiredFields) {
        Bundle bundle = new Bundle();
        for (AutowiredField autowiredField : autowiredFields) {
            String fieldName = autowiredField.getFieldName();
            String paramsValue = params.get(fieldName);
            if (paramsValue == null) {
                // 返回默认值
                if (TextUtils.isEmpty(autowiredField.getValue())) {
                    return null;
                } else {
                    paramsValue = autowiredField.getValue();
                }
            }
            try {
                switch (autowiredField.getFieldType()) {
                    case "int":
                        bundle.putInt(autowiredField.getFieldName(), Integer.parseInt(paramsValue));
                        break;
                    case "long":
                        bundle.putLong(autowiredField.getFieldName(), Long.parseLong(paramsValue));
                        break;
                    case "char":
                        if (TextUtils.isEmpty(paramsValue) || paramsValue.length() > 1) {
                            throw new NumberFormatException();
                        } else {
                            bundle.putChar(autowiredField.getFieldName(), paramsValue.charAt(0));
                        }
                        break;
                    case "short":
                        bundle.putShort(autowiredField.getFieldName(), Short.parseShort(paramsValue));
                        break;
                    case "byte":
                        bundle.putByte(autowiredField.getFieldName(), Byte.parseByte(paramsValue));
                        break;
                    case "float":
                        bundle.putFloat(autowiredField.getFieldName(), Float.parseFloat(paramsValue));
                        break;
                    case "double":
                        bundle.putDouble(autowiredField.getFieldName(), Double.parseDouble(paramsValue));
                        break;
                    case "boolean":
                        if (TextUtils.isEmpty(paramsValue) || !"true".equals(paramsValue) || !"false".equals(paramsValue)) {
                            throw new NumberFormatException();
                        } else {
                            bundle.putBoolean(autowiredField.getFieldName(), Boolean.parseBoolean(paramsValue));
                        }
                        break;
                    default:
                        // 验证json是否有效
                        JSONObject jsonObject = new JSONObject(paramsValue);
                        bundle.putString(autowiredField.getFieldName(), paramsValue);
                        break;
                }
            } catch (NumberFormatException e) {
                // 格式不符合
                return null;
            } catch (JSONException e) {
                // 所传json不符合
                return null;
            }
        }
        return bundle;
    }
}
