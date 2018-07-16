package com.spearbothy.router.api.router;

import com.spearbothy.router.entity.RouteEntity;

/**
 * @author mahao
 * @date 2018/7/16 上午10:39
 * @email zziamahao@163.com
 */

public class Response {

    // error_code
    public static final int CODE_SUCCESS = 0;

    public static final int CODE_FAIL_PROTOCOL = -1;

    public static final int CODE_FAIL_ROUTER_NOT_FOUND = -2;

    private int errorCode;

    private String errorMessage;

    // 查询结果
    private RouteEntity entity;


    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public RouteEntity getEntity() {
        return entity;
    }

    public void setEntity(RouteEntity entity) {
        this.entity = entity;
    }

    public void setError(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public void setSuccess(RouteEntity entity) {
        this.errorCode = CODE_SUCCESS;
        this.entity = entity;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return errorCode == CODE_SUCCESS && entity != null;
    }
}
