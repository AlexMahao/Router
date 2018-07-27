package com.spearbothy.router.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author mahao
 * @date 2018/7/24 上午11:56
 * @email zziamahao@163.com
 */

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface Autowired {

    String desc() default "";
}
