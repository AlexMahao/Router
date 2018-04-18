package com.spearbothy.router.api;

import java.util.regex.Pattern;

/**
 * Created by android-dev on 2018/4/18.
 */

public class Util {

    // 判断协议是否合法
    public static boolean isUrl(String url) {
        String regex = "^router://.*(\\?(.+=.+)+)?$";
        return Pattern.matches(regex, url);
    }
}
