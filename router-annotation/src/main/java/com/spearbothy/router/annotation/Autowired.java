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

    /**
     * @return 默认值，如果设置默认值则参数可以不传，会取默认值
     */
    String value() default "";

    /**
     * 适配历史代码
     * @return 如果enble为false，则仅做参数校验和传入，不做获取和异常恢复
     */
    boolean enable() default true;
}
