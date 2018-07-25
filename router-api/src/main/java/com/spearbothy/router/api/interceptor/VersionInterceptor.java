package com.spearbothy.router.api.interceptor;

import android.text.TextUtils;

import com.spearbothy.router.api.entity.RouteAddition;
import com.spearbothy.router.api.router.Response;
import com.spearbothy.router.entity.RouteEntity;

/**
 * @author mahao
 * @date 2018/7/10 下午3:22
 * @email zziamahao@163.com
 */

public class VersionInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) {
        Response response = chain.proceed(chain.request());
        if (response.isSuccess()) {
            RouteAddition entity = response.getEntity();
            String activityVersion = entity.getVersion();
            String pathVersion = chain.request().getParams().get("version");
            if (!isSupportVersion(activityVersion, pathVersion)) {
                response.setError(Response.CODE_FAIL_PROTOCOL, "跳转地址版本过低（" + pathVersion + "），不再支持，当前协议版本：" + activityVersion + "");
            }
        }
        return response;
    }

    /**
     * 如果activityVersion 被修改，则说明之前的路由协议及pathVersion<= old ActivityVersion不再生效。即不再支持
     *
     * @param activityVersion activity路由协议最新被修改的版本
     * @param pathVersion     当前路由协议指定的版本，
     * @return activityVersion <= pathVersion
     */
    private boolean isSupportVersion(String activityVersion, String pathVersion) {
        if (TextUtils.isEmpty(activityVersion)) {
            return true;
        }

        if (TextUtils.isEmpty(pathVersion)) {
            return true;
        }
        String[] aVersions = activityVersion.split("\\.");
        String[] pVersions = pathVersion.split("\\.");
        int length = aVersions.length > pVersions.length ? pVersions.length : aVersions.length;
        for (int i = 0; i < length; i++) {
            if (Integer.parseInt(aVersions[i]) > Integer.parseInt(pVersions[i])) {
                return false;
            } else if (Integer.parseInt(aVersions[i]) < Integer.parseInt(pVersions[i])) {
                return true;
            }
        }

        if (aVersions.length < pVersions.length) {
            for (int i = aVersions.length; i < pVersions.length; i++) {
                if (Integer.parseInt(pVersions[i]) > 0) {
                    return true;
                }
            }
        } else {
            for (int i = pVersions.length; i < aVersions.length; i++) {
                if (Integer.parseInt(aVersions[i]) > 0) {
                    return false;
                }
            }
        }
        return true;
    }
}
