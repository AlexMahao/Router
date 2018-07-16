package com.spearbothy.router.api;

import com.spearbothy.router.api.router.Response;
import com.spearbothy.router.api.util.Logger;

/**
 * Created by android-dev on 2018/4/18.
 */

public abstract class ResultCallback {
    public abstract void onSuccess();

    public void onError(Response response) {
        if (Router.DEBUG) {
            Logger.error(response.toString());
        }
    }
}
