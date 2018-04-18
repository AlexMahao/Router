package com.spearbothy.router.api;

/**
 * Created by android-dev on 2018/4/18.
 */

public class RouterResponse {

    /**
     * 协议相关错误（协议地址，参数，路由标识）
     */
    public static final int ERROR_PROTOCOL = 100001;

    private int code;
    private String desc;

    public RouterResponse(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "RouterResponse{" +
                "code=" + code +
                ", desc=" + desc +
                '}';
    }
}
