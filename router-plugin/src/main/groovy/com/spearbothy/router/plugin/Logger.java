package com.spearbothy.router.plugin;

/**
 * @author mahao
 * @date 2018/7/24 上午11:19
 * @email zziamahao@163.com
 */

public class Logger {

    public static void info(String msg) {
        System.out.println(msg);
    }


    public static void infoLine(String msg) {
        System.out.println("\n======================= " + msg + " =======================");
    }

    public static void error(String msg) {
        System.err.println(msg);
    }

    public static void error(String msg, Throwable throwable) {
        System.err.println(msg + "\n" + throwable.getMessage());
    }
}
