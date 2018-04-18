package com.spearbothy.router.api;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

/**
 * 扩展URL协议，支持自定义协议,仅做解析，不支持请求
 * Created by android-dev on 2018/4/18.
 */
class RouterURLStreamHandlerFactory implements URLStreamHandlerFactory {
    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if (RouterClient.ROUTER_PROTOCOL.equals(protocol)) {
            return new URLStreamHandler() {
                @Override
                protected URLConnection openConnection(URL u) throws IOException {
                    return null;
                }
            };
        }
        return null;
    }
}
