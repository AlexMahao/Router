package com.spearbothy.router.api;

import android.content.Context;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 路由请求
 * 在这里处理数据，解析成对应实体
 * Created by android-dev on 2018/3/28.
 */
public class RouterRequest {
    private Context context;
    private String url;
    private String protocol;
    private String host;
    private String path;
    private Map<String, String> params = new HashMap<>();
    private ResultCallback callback;

    RouterRequest(Context context) {
        this.context = context;
    }

    public RouterRequest url(String url) {
        this.url = url;
        return this;
    }

    public void start() {
        start(null);
    }


    public void start(ResultCallback resultCallback) {
        callback = resultCallback;
        // 先进行数据解析
        try {
            URL url = new URL(getUrl());
            this.host = url.getHost();
            this.protocol = url.getProtocol();
            this.path = url.getPath();
            loadUrlParams(url.getQuery(), params);
            // 跳转RouterManager进行处理
            RouterManager.process(this);
        } catch (MalformedURLException e) {
            RouterLog.error("协议不合法：" + getUrl(), e);
            if (callback != null) {
                callback.onError(new RouterResponse(RouterResponse.ERROR_PROTOCOL, "协议不合法"));
            }
        }
    }
/*

    private boolean parserUrl(String url) {
        // 匹配是否是url ，
        if (!Util.isUrl(url)) {
            RouterLog.error("协议不合法：" + getUrl());
            if (callback != null) {
                callback.onError(new RouterResponse(RouterResponse.ERROR_PROTOCOL, "协议不合法"));
            }
            return false;
        }

        // 解析数据


    }
*/


    private void loadUrlParams(String param, Map<String, String> map) {
        if (param == null) {
            return;
        }
        String[] params = param.split("&");
        for (int i = 0; i < params.length; i++) {
            String[] p = params[i].split("=");
            if (p.length == 2) {
                map.put(p[0], p[1]);
            }
        }
    }

    public Context getContext() {
        return context;
    }

    public String getUrl() {
        return url;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public ResultCallback getCallback() {
        return callback;
    }
}
