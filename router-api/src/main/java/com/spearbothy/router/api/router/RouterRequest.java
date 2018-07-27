package com.spearbothy.router.api.router;

import android.content.Context;

import com.spearbothy.router.api.ResultCallback;
import com.spearbothy.router.api.util.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mahao
 * @date 2018/6/27 下午3:19
 * @email zziamahao@163.com
 */

public class RouterRequest {
    private Context context;
    private String url;
    private String protocol;
    private String host;
    private String path;
    private Map<String, String> params = new HashMap<>();
    private ResultCallback callback;

    public RouterRequest(Context context) {
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
        try {
            URL url = new URL(getUrl());
            this.host = url.getHost();
            this.protocol = url.getProtocol();
            this.path = url.getPath();
            loadUrlParams(url.getQuery(), params);
            Logger.info("url 解析结果：" + toString());
            RouterClient.process(this);
        } catch (MalformedURLException e) {
            Logger.error("协议不合法：" + getUrl(), e);
            if (callback != null) {
                Response response = new Response();
                response.setError(Response.CODE_FAIL_PROTOCOL, "协议不合法");
                callback.onError(response);
            }
        }
    }


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

    @Override
    public String toString() {
        return "RouterRequest{" +
                "url='" + url + '\'' +
                ", protocol='" + protocol + '\'' +
                ", host='" + host + '\'' +
                ", path='" + path + '\'' +
                ", params=" + params +
                '}';
    }
}
