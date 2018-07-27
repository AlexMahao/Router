package com.spearbothy.router.api.router;

import com.spearbothy.router.api.entity.ResponseResult;
import com.spearbothy.router.api.entity.RouteAddition;

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

    public static final int CODE_FAIL_VERSION_NOT_SUPPORT = -3;

    public static final int CODE_FAIL_PARAMS_NOT_VALID = -4;

    private int errorCode;

    private String errorMessage;

    // 查询结果
    private ResponseResult result;


    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public ResponseResult getResult() {
        return result;
    }

    public void setResult(ResponseResult result) {
        this.result = result;
    }

    public void setError(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public void setSuccess(ResponseResult entity) {
        this.errorCode = CODE_SUCCESS;
        this.result = entity;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return errorCode == CODE_SUCCESS && result != null;
    }
}
