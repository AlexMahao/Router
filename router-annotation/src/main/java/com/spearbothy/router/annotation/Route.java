package com.spearbothy.router.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by android-dev on 2018/3/28.
 */

@Retention(RetentionPolicy.CLASS) // 定义注解在哪一个级别可用
@Target(ElementType.TYPE) // 注解的目标
public @interface Route {

    String path();

    String desc();

    String version(); // 支持版本
}
