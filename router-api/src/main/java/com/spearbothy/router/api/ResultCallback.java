package com.spearbothy.router.api;

/**
 * Created by android-dev on 2018/4/18.
 */

public interface ResultCallback {
    void onSuccess();

    void onError(RouterResponse response);
}
