package com.spearbothy.router.compiler.util;

/**
 * Created by android-dev on 2018/6/26.
 */

public class StringUtils {

    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }

    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }
}
