package com.spearbothy.router.sample;

import android.widget.Toast;

import com.spearbothy.router.BaseDebugListActivity;
import com.spearbothy.router.BaseRecyclerAdapter;
import com.spearbothy.router.annotation.Route;
import com.spearbothy.router.api.ResultCallback;
import com.spearbothy.router.api.Router;
import com.spearbothy.router.api.router.Response;

import java.util.List;

@Route(path = "/main", desc = "首页", version = "1.0.0")
public class MainActivity extends BaseDebugListActivity {

    @Override
    public void initListItem(List<Item> items) {
        items.add(new Item("当前Module跳转", "带有参数"));
        items.add(new Item("当前Module跳转", "带有参数  需要登录"));
        items.add(new Item("跳转测试module", ""));
        items.add(new Item("当前module跳转，以方法的方式", "带有参数"));
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter.BaseViewHolder holder, int position) {
        switch (position) {
            case 0:
                Router.with(this)
                        .url("router://app/params?version=1.0&index=2")
                        .start(new ResultCallback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(getApplicationContext(), "页面跳转成功", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Response response) {
                                Toast.makeText(getApplicationContext(), response.getErrorMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
            case 1:
                Router.with(this)
                        .url("router://app/params?version=1.0&index=2&needLogin=true")
                        .start(new ResultCallback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(getApplicationContext(), "页面跳转成功", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Response response) {
                                Toast.makeText(getApplicationContext(), response.getErrorMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
            case 2:
                Router.with(this)
                        .url("router://test/main?version=0.9")
                        .start(new ResultCallback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(getApplicationContext(), "页面跳转成功", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Response response) {
                                Toast.makeText(getApplicationContext(), response.getErrorMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
            case 3:
                Router.with(this)
                        .module("app")
                        .path("/params")
                        .param("index", "4")
                        .start();
                break;
            default:
                break;
        }
    }
}
