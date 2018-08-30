package com.spearbothy.router.sample;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.spearbothy.router.BaseDebugListActivity;
import com.spearbothy.router.BaseRecyclerAdapter;
import com.spearbothy.router.annotation.Route;
import com.spearbothy.router.api.ResultCallback;
import com.spearbothy.router.api.Router;
import com.spearbothy.router.api.router.Response;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

@Route(path = "/main", desc = "首页", version = "1.0.0")
public class MainActivity extends BaseDebugListActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_CODE = 3;

    private Handler handler = new Handler();

    String url = "router://app/webView?url=https://www.baidu.com/s?ie=utf-8&f=8&rsv_bp=0&rsv_idx=1&tn=baidu&wd=16%E8%BF%9B%E5%88%B6%E8%BD%AC10%E8%BF%9B%E5%88%B6&rsv_pq=9955975600005477&rsv_t=6f0fXz6O8qdRtaS4QwbIegnM3xO6uyAs3Ny7ihflTcc%2B6ODtPN6nu5DFxT4&rqlang=cn&rsv_enter=1&rsv_sug3=3&rsv_sug1=3&rsv_sug7=100";


    @Override
    public void initListItem(List<Item> items) {
        items.add(new Item("以url的方式完成跳转", "", "", Item.GROUP));
        items.add(new Item("跳转当前module，目标页面需要参数，参数声明了默认值", "router://app/params", "router_module"));
        items.add(new Item("跳转当前module，传入参数", "router://app/params?index=2", "router_module_params"));
        items.add(new Item("跳转当前module，传入参数,User对象", "router://app/user_params?user={\"age\":18,\"name\":\"test\"}&index=3", "router_module_params_obj"));
        items.add(new Item("跳转当前module，跳转页面需要登录", "router://app/params?index=2&needLogin=true", "router_module_need_login"));
        items.add(new Item("跳转测试module", "router://test/main", "router_test"));
        items.add(new Item("跳转当前module，测试flag", "router://test/main & router://app/main?intentFlag=[67108864]", "router_flag"));
        items.add(new Item("跳转webView", "router://app/webView?url=https://www.baidu.com/s?ie=utf-8&f=8&rsv_bp=0&rsv_idx=1&tn=baidu&wd=16%E8%BF%9B%E5%88%B6%E8%BD%AC10%E8%BF%9B%E5%88%B6&rsv_pq=9955975600005477&rsv_t=6f0fXz6O8qdRtaS4QwbIegnM3xO6uyAs3Ny7ihflTcc%2B6ODtPN6nu5DFxT4&rqlang=cn&rsv_enter=1&rsv_sug3=3&rsv_sug1=3&rsv_sug7=100", "router_webView"));
        items.add(new Item("跳转activity,指定requestCode", "router://app/request_code?activityReqCode=" + REQUEST_CODE, "router_activity_result"));


        items.add(new Item("以method的方式进行跳转", "", "", Item.GROUP));
        items.add(new Item("跳转当前module，目标页面需要参数，参数声明了默认值", "", "method_module"));
        items.add(new Item("跳转当前module，传入参数", "", "method_module_params"));
        items.add(new Item("跳转当前module，传入参数,User对象", "", "method_module_params_obj"));
        items.add(new Item("跳转当前module，跳转页面需要登录", "", "method_module_need_login"));
        items.add(new Item("跳转测试module", "", "method_test"));
        items.add(new Item("跳转当前module，测试flag", "", "method_flag"));
        items.add(new Item("跳转webView", "", "method_webview"));
        items.add(new Item("跳转activity,指定requestCode", "", "method_activity_result"));
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter.BaseViewHolder holder, Item item, String id) {
        switch (id) {
            case "router_module":
                Router.with(this)
                        .url("router://app/params")
                        .start(new SimpleResultCallback());
                break;
            case "router_module_params":
                Router.with(this)
                        .url("router://app/params?index=2")
                        .start(new SimpleResultCallback());
                break;
            case "router_module_need_login":
                Router.with(this)
                        .url("router://app/params?index=2&needLogin=true")
                        .start(new SimpleResultCallback());
                break;
            case "router_test":
                Router.with(this)
                        .url("router://test/main")
                        .start();
                break;
            case "router_flag":
                Router.with(this)
                        .url("router://test/main")
                        .start(new SimpleResultCallback());

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //跳转首页，传入intent Flag CLEAR_TOP
                        Router.with(MainActivity.this)
                                .url("router://app/main?intentFlag=[67108864,536870912]")
                                .start(new SimpleResultCallback());
                    }
                }, 1000);
                break;
            case "router_module_params_obj":
                Router.with(this)
                        .url("router://app/user_params?user={\"age\":18,\"name\":\"test\"}&index=3")
                        .start();
                break;
            case "router_webView":
                try {
                    Router.with(this)
                            .url("router://app/webview?encode=true&url=" + URLEncoder.encode(this.url, "utf-8"))
                            .start(new SimpleResultCallback());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case "router_activity_result":
                Router.with(this)
                        .url("router://app/request_code?activityReqCode=" + REQUEST_CODE)
                        .start(new SimpleResultCallback());
                break;

            case "method_module":
                Router.with(this)
                        .module("app")
                        .path("/params")
                        .start(new SimpleResultCallback());
                break;
            case "method_module_params":
                Router.with(this)
                        .module("app")
                        .path("/params")
                        .param("index", "2")
                        .start(new SimpleResultCallback());
                break;
            case "method_module_need_login":
                Router.with(this)
                        .module("app")
                        .path("/params")
                        .param("index", "2")
                        .param("needLogin", "true")
                        .start(new SimpleResultCallback());
                break;
            case "method_test":
                Router.with(this)
                        .module("test")
                        .path("/main")
                        .start(new SimpleResultCallback());
                break;
            case "method_flag":
                Router.with(this)
                        .module("test")
                        .path("/main")
                        .start();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Router.with(MainActivity.this)
                                .module("app")
                                .path("/main")
                                .addIntentFlag(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                .addIntentFlag(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                .start();
                    }
                }, 1000);
                break;
            case "method_webview":
                Router.with(this)
                        .module("app")
                        .path("/webview")
                        .param("url", url)
                        .param("encode", "false")
                        .start();
                break;
            case "method_module_params_obj":
                User user = new User("测试", 18);
                Router.with(this)
                        .module("app")
                        .path("/user_params")
                        .param("user", JSON.toJSONString(user))
                        .start(new SimpleResultCallback());
                break;
            case "method_activity_result":
                Router.with(this)
                        .module("app")
                        .path("/request_code")
                        .activityRequest(REQUEST_CODE)
                        .start();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Toast.makeText(this, "activity 回调", Toast.LENGTH_SHORT).show();
        }
    }

    class SimpleResultCallback extends ResultCallback {
        @Override
        public void onSuccess() {
            Toast.makeText(getApplicationContext(), "页面跳转成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(Response response) {
            Toast.makeText(getApplicationContext(), response.getErrorMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(Response response) {
            Toast.makeText(getApplicationContext(), "onCancel", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(TAG, "onNewIntent");
    }
}
