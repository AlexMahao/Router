package com.spearbothy.router.compiler;

import com.google.auto.service.AutoService;
import com.spearbothy.router.annotation.Router;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * 实现逻辑：
 * - 生成辅助类
 * -
 */
@AutoService(Processor.class)
public class RouterProcess extends AbstractProcessor {
    // 日志相关
    private Messager mMessager;

    private static final String KEY_PACKAGE = "packageName";

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        mMessager = processingEnv.getMessager();
        if (!roundEnvironment.processingOver()) {
            processRouter(roundEnvironment);
        }
        return true;
    }

    private void processRouter(RoundEnvironment roundEnvironment) {
        String packageName = processingEnv.getOptions().get(KEY_PACKAGE);
        String targetClass = packageName + ".RouterLoaderImpl";
        Map<String, Router> routerMap = new HashMap<>();
        if (!Utils.isEmpty(packageName)) {
            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Router.class);
            for (Element element : elements) {
                // 检查element的类型
                if (checkAnnotationValid(element, Router.class)) {
                    TypeElement typeElement = (TypeElement) element;
                    Router annotation = typeElement.getAnnotation(Router.class);
                    routerMap.put(typeElement.getQualifiedName().toString(), annotation);
                }
            }
            // 写入类
            try {
                // processingEnv ： 注解处理环境（工具类），提供很多有用的功能工具类
                JavaFileObject jfo = processingEnv.getFiler().createSourceFile(targetClass);
                Writer writer = jfo.openWriter();
                writer.write(generateJavaCode(packageName, routerMap));
                writer.flush();
                writer.close();
                //----- 在生成类之后gradle会对代码进行优化
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            error(mMessager, "router 未获取到packageName");
        }
    }


    private String generateJavaCode(String packageName, Map<String, Router> routerMap) {
        StringBuilder sb = new StringBuilder()
                .append("package " + packageName + ";")
                .append("import com.spearbothy.router.api.IRouterLoader;\n")
                .append("import java.util.Map;\n")
                .append("import com.spearbothy.router.RouterEntity;\n")
                .append("public class RouterLoaderImpl implements IRouterLoader {\n\n")
                .append("public void init(Map<String, RouterEntity> map) {\n");
        for (Map.Entry<String, Router> entry : routerMap.entrySet()) {
            String key = entry.getKey();
            Router value = entry.getValue();
            sb.append("map.put(\"" + value.path() + "\",new RouterEntity(" + entry.getKey() + ".class, \"" + value.desc() + "\"));");
        }
        sb.append("\n}\n\n}");
        return sb.toString();
    }

    private boolean checkAnnotationValid(Element annotatedElement, Class clazz) {
        if (annotatedElement.getKind() != ElementKind.CLASS) {
            error(mMessager, "%s must be declared on Class.", clazz.getSimpleName());
            return false;
        }
        return true;
    }

    private static void note(Messager messager, CharSequence note) {
        messager.printMessage(Diagnostic.Kind.NOTE, note);
    }

    // 展示错误信息
    private void error(Messager messager, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        mMessager.printMessage(Diagnostic.Kind.NOTE, message);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
            types.add(annotation.getCanonicalName());
        }
        return types;
    }

    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
        annotations.add(Router.class);
        return annotations;
    }
}
