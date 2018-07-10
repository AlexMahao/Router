package com.spearbothy.router.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by android-dev on 2018/3/28.
 */

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Route {

    String path();

    String desc();

    String version(); // 支持版本

//
//    boolean download2Store() default false; // 是否跳转应用商城
//
//    // 业务参数，暂不考虑
//    boolean needLogin() default false; // 判断是否登录
//
//    // 整体路径分为两部分，第一部分对activity的声明。
//    // 具体路径跳转
}
