package com.spearbothy.router.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.spearbothy.router.R;
import com.spearbothy.router.annotation.Autowired;
import com.spearbothy.router.annotation.Route;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author mahao
 * @date 2018/8/15 下午4:12
 */

@Route(path = "/webview", version = "1.0.0", desc = "webView")
public class WebActivity extends AppCompatActivity {

    @Autowired
    public String url;

    @Autowired
    public boolean encode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        if (encode) {
            try {
                url = URLDecoder.decode(url, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        ((WebView) findViewById(R.id.webView)).loadUrl(url);
    }
}
