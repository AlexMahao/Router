package com.spearbothy.router.compiler.processor;

import com.alibaba.fastjson.JSON;
import com.google.auto.service.AutoService;
import com.spearbothy.router.annotation.Route;
import com.spearbothy.router.compiler.entity.RouterDetail;
import com.spearbothy.router.compiler.util.Constants;
import com.spearbothy.router.compiler.util.Logger;
import com.spearbothy.router.compiler.util.StringUtils;
import com.spearbothy.router.entity.RouteEntity;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;


@AutoService(Processor.class)
@SupportedOptions(Constants.MODULE_NAME_KEY)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class RouterProcess extends AbstractProcessor {

    private Filer filer;
    private String moduleName;
    private Logger logger;
    private static final String WORKING_DIR = System.getProperty("user.dir"); // 项目目录

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        filer = processingEnv.getFiler(); // 生成class
        logger = new Logger(processingEnv.getMessager());

        Map<String, String> options = processingEnv.getOptions();

        if (!options.isEmpty()) {
            moduleName = options.get(Constants.MODULE_NAME_KEY);
        }
        if (StringUtils.isNotEmpty(moduleName)) {
            logger.info("The user has configuration the module name, it was [" + moduleName + "]");
        } else {
            logger.error("These no module name");
            throw new RuntimeException("please add moduleName");
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
        if (set != null && !set.isEmpty()) {
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Route.class);
            try {
                processRouter(elements);
            } catch (IOException e) {
                logger.error(e);
            }
            return true;
        }
        return false;
    }

    private void processRouter(Set<? extends Element> elements) throws IOException {
        Map<TypeElement, Route> routerMap = new HashMap<>();
        if (elements != null && !elements.isEmpty()) {
            for (Element element : elements) {
                // 检查element的类型
                TypeElement typeElement = (TypeElement) element;
                Route annotation = typeElement.getAnnotation(Route.class);
                routerMap.put(typeElement, annotation);
            }
        }

        if (!routerMap.isEmpty()) {
            logger.info(moduleName + " init ...");
            // 生成清单文件
            RouterDetail routerDetail = new RouterDetail();
            routerDetail.setName(moduleName);

            ClassName InterfaceName = ClassName.get(Constants.LOADER_INTERFACE_PACKAGE, Constants.LOADER_INTERFACE_NAME);

            ParameterizedTypeName routerMapType = ParameterizedTypeName.get(
                    ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ClassName.get(RouteEntity.class)
            );

            ParameterSpec parameterSpec = ParameterSpec.builder(routerMapType, "root").build();

            MethodSpec.Builder loadIntoBuilder = MethodSpec.methodBuilder(Constants.LOADER_INTERFACE_LOADINTO)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(parameterSpec);

            for (Map.Entry<TypeElement, Route> entry : routerMap.entrySet()) {
                TypeElement element = entry.getKey();
                Route value = entry.getValue();
                loadIntoBuilder.addStatement("root.put($S, new $T($T.class, $S, $S))", value.path(), RouteEntity.class, ClassName.get(element), value.desc(), value.version());

                // 输出路由清单
                RouterDetail.Path path = new RouterDetail.Path(ClassName.get(element).packageName() + ClassName.get(element).simpleName());
                path.setPath(value.path(), moduleName, Constants.ROUTER_PROTOCOL);
                path.addParams("version", value.version());
                path.setDesc(value.desc());
                routerDetail.addPath(path);
            }

            // 写入当前编译
            MethodSpec loadInto = loadIntoBuilder.build();

            MethodSpec getModuleName = MethodSpec.methodBuilder(Constants.LOADER_INTERFACE_GET_MODULE_NAME)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("return $S", moduleName)
                    .returns(String.class)
                    .build();

            TypeSpec typeSpec = TypeSpec.classBuilder(Constants.ROUTER_LOADER_CLASS_NAME + moduleName)
                    .addSuperinterface(InterfaceName)
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(loadInto)
                    .addMethod(getModuleName)
                    .build();

            JavaFile javaFile = JavaFile.builder(Constants.ROUTER_LOADER_PACKAGE, typeSpec)
                    .build();

            javaFile.writeTo(filer);

            logger.infoLine("生成类信息");
            logger.info(typeSpec.toString());
            logger.infoLine("");
            logger.info(moduleName + " success !!! ");

            // 保存清单文件
            save(JSON.toJSONString(routerDetail, true));
        }
    }

    private void save(String detail) {
        try {
            List<String> lines = Arrays.asList(detail);
            Files.createDirectories(Paths.get(Constants.ROUTER_DETAIL_DIR));
            Path file = Paths.get(Constants.ROUTER_DETAIL_DIR + File.separator + moduleName + ".json");
            logger.info("协议清单文件 PATH: " + file.toAbsolutePath());
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        annotations.add(Route.class);
        return annotations;
    }
}
