package com.spearbothy.router.compiler.processor;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;

/**
 *
 * 处理自动注入
 * @author mahao
 * @date 2018/7/19 下午3:02
 * @email zziamahao@163.com
 */
public class AutowiredProcess extends AbstractProcessor{


    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        return false;
    }
}
