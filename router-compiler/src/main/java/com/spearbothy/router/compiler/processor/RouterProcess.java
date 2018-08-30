package com.spearbothy.router.compiler.processor;

import com.alibaba.fastjson.JSON;
import com.google.auto.service.AutoService;
import com.spearbothy.router.annotation.Autowired;
import com.spearbothy.router.annotation.Route;
import com.spearbothy.router.compiler.entity.Addition;
import com.spearbothy.router.compiler.entity.AutowiredField;
import com.spearbothy.router.compiler.entity.RouteClass;
import com.spearbothy.router.compiler.entity.RouterDetail;
import com.spearbothy.router.compiler.util.BundleStateHelper;
import com.spearbothy.router.compiler.util.Constants;
import com.spearbothy.router.compiler.util.Logger;
import com.spearbothy.router.compiler.util.StringUtils;
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
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;


@AutoService(Processor.class)
@SupportedOptions(Constants.MODULE_NAME_KEY)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
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
        logger.info("option:" + options.toString());
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
            // 处理所有注解
            List<Addition> additionList = new ArrayList<>();

            initRoute(additionList, roundEnv);
            initAutowird(additionList, roundEnv);

            try {
                generatorAutowiredFile(additionList);
                generatorRouteFile(additionList);
                // 生成清单文件
                generatorMainfest(additionList);
            } catch (IOException e) {
                logger.error(e);
            }
            return true;
        }
        return false;
    }

    /**
     * 根据所有类的注解信息，生成清单文件，便于查阅
     */
    private void generatorMainfest(List<Addition> additionList) {
        RouterDetail detail = new RouterDetail();
        detail.setName(moduleName);

        for (Addition addition : additionList) {
            RouterDetail.Path path = new RouterDetail.Path(addition.getQualifiedName());
            path.setPath(addition.getRouteClass().getPath(), moduleName, Constants.ROUTER_PROTOCOL);
            path.setDesc(addition.getRouteClass().getDesc());
            path.setVersion(addition.getRouteClass().getVersion());
            path.setParams(addition.getAutowiredFields());
            detail.addPath(path);
        }

        try {
            List<String> lines = Arrays.asList(JSON.toJSONString(detail, true));
            Files.createDirectories(Paths.get(Constants.ROUTER_DETAIL_DIR));
            Path file = Paths.get(Constants.ROUTER_DETAIL_DIR + File.separator + moduleName + ".json");
            logger.info("协议清单文件 PATH: " + file.toAbsolutePath());
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成加载路由协议辅助类，目标路径pcom.huli.android.router.loader.RouterLoader$$XXX.java
     */
    private void generatorRouteFile(List<Addition> additionList) throws IOException {

        ClassName InterfaceName = ClassName.get(Constants.LOADER_INTERFACE_PACKAGE, Constants.LOADER_INTERFACE_NAME);

        ClassName routeAddition = ClassName.get(Constants.ROUTER_PACKAGE + ".api.entity", "RouteAddition");

        ParameterizedTypeName routerMapType = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                routeAddition
        );

        ParameterSpec parameterSpec = ParameterSpec.builder(routerMapType, "root").build();

        // loadInto Method
        MethodSpec.Builder loadIntoBuilder = MethodSpec.methodBuilder(Constants.LOADER_INTERFACE_LOADINTO)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(parameterSpec);

        loadIntoBuilder.addStatement("$T routeAddition = null", routeAddition);
        for (Addition addition : additionList) {
            RouteClass routeClass = addition.getRouteClass();
            if (routeClass != null) {
                loadIntoBuilder.addStatement("routeAddition = new $T($T.class, $S, $S)", routeAddition, routeClass.getClassName(), routeClass.getDesc(), routeClass.getVersion());
                for (AutowiredField field : addition.getAutowiredFields()) {
                    loadIntoBuilder.addStatement("routeAddition.addAutowiredField($S, $S, $S, $S, $L)", field.getFieldName(), field.getFieldType(), field.getDesc(), field.getValue(),field.isEnable());
                }
                loadIntoBuilder.addStatement("root.put($S, routeAddition)", routeClass.getPath());
            }
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

        // 添加logger信息
        logger.infoLine("生成类信息");
        logger.info(typeSpec.toString());
        logger.infoLine("");
        logger.info(moduleName + " success !!! ");
    }

    /**
     * 生成注解字段辅助类，格式如下：com.huli.android.router.test.XXXX$$RouteAutowired
     */
    private void generatorAutowiredFile(List<Addition> additions) {
        for (Addition addition : additions) {
            if (addition.getAutowiredFields().isEmpty()) {
                continue;
            }
            // 生成java文件
            // 构造 onSaveInstance(Activity instance, Bundle outState){
            //    outState.putXXX(name, instance.name);
            // }
            MethodSpec.Builder saveInstanceBuilder = MethodSpec.methodBuilder("onSaveInstanceState")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(void.class)
                    .addParameter(ClassName.bestGuess(addition.getQualifiedName()), "instance")
                    .addParameter(Constants.CLASS_BUNDLE, "outState");

            for (AutowiredField field : addition.getAutowiredFields()) {
                BundleStateHelper.statementSaveValueIntoBundle(saveInstanceBuilder, field.getFieldName(), field.getFieldType(), "instance", "outState");
            }
            MethodSpec saveInstanceMethod = saveInstanceBuilder.build();

            // 构造 onRestoreInstanceState(Activity instance, Bundle outState){
            //    instance.name = outState.getXXX(name);
            // }
            MethodSpec.Builder restoreBuilder = MethodSpec.methodBuilder("onRestoreInstanceState")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(void.class)
                    .addParameter(ClassName.bestGuess(addition.getQualifiedName()), "instance")
                    .addParameter(Constants.CLASS_BUNDLE, "outState");
            for (AutowiredField field : addition.getAutowiredFields()) {
                if (field.isEnable()) {

                    BundleStateHelper.statementGetValueFromBundle(restoreBuilder, field.getFieldName(), field.getFieldType(), "instance", "outState");
                }
            }
            MethodSpec restoreMethod = restoreBuilder.build();

            //生成类
            TypeSpec saveStateClass = TypeSpec.classBuilder(addition.getSimpleName() + Constants.GENERATED_FILE_SUFFIX)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addMethod(saveInstanceMethod)
                    .addMethod(restoreMethod)
                    .build();


            JavaFile javaFile = JavaFile.builder(addition.getPackageName(), saveStateClass)
                    .build();

            try {
                javaFile.writeTo(filer);

                logger.infoLine("生成类信息");
                logger.info(saveStateClass.toString());
                logger.infoLine("");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 加载@Autowired注解的类信息
     * @param additions 保存的实体
     * @param roundEnv 获取注解的对象
     */
    private void initAutowird(List<Addition> additionList, RoundEnvironment roundEnv) {
        Set<? extends Element> autowiredElements = roundEnv.getElementsAnnotatedWith(Autowired.class);
        if (autowiredElements != null && !autowiredElements.isEmpty()) {
            for (Element element : autowiredElements) {
                VariableElement variableElement = (VariableElement) element;
                TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
                String qualifiedName = typeElement.getQualifiedName().toString();
                if (checkIsSubClassOf(typeElement, Constants.CLASS_ACTIVITY, Constants.CLASS_FRAGMENT_ACTIVITY)) {

                    Addition addition = findAddition(additionList, qualifiedName);
                    if (addition == null) {
                        addition = new Addition();
                        addition.setQualifiedName(qualifiedName);
                        additionList.add(addition);
                    }

                    Autowired autowired = variableElement.getAnnotation(Autowired.class);
                    AutowiredField field = new AutowiredField();
                    field.setFieldName(variableElement.getSimpleName().toString());
                    field.setFieldType(variableElement.asType().toString());
                    field.setDesc(autowired.desc());
                    field.setValue(autowired.value());
                    field.setEnable(autowired.enable());
                    addition.addAutowiredField(field);
                }
            }
        }
    }

    /**
     * 加载@Route注解的类信息
     * @param additions 保存的实体
     * @param roundEnv 获取注解的对象
     */
    private void initRoute(List<Addition> additions, RoundEnvironment roundEnv) {
        Set<? extends Element> routeElements = roundEnv.getElementsAnnotatedWith(Route.class);
        if (routeElements != null && !routeElements.isEmpty()) {
            for (Element element : routeElements) {
                // 获取注解信息
                TypeElement typeElement = (TypeElement) element;
                Route route = typeElement.getAnnotation(Route.class);

                // 查询该类是否已经创建并保存信息
                String qualifiedName = typeElement.getQualifiedName().toString();
                Addition addition = findAddition(additions, qualifiedName);
                if (addition == null) {
                    addition = new Addition();
                    addition.setQualifiedName(qualifiedName);
                    additions.add(addition);
                }

                // 保存route的信息
                RouteClass routeClass = new RouteClass();
                routeClass.setClassName(ClassName.get(typeElement));
                routeClass.setDesc(route.desc());
                routeClass.setPath(route.path());
                routeClass.setVersion(route.version());
                addition.setRouteClass(routeClass);
            }
        }
    }

    /**
     * 查询当前类的注解信息是否已存在
     * @param additions 查询的集合
     * @param qualifiedName 全路径类名
     * @return 如果查询到，则返回对应信息，如果未查询到，则返回null
     */
    public Addition findAddition(List<Addition> additions, String qualifiedName) {
        for (Addition addition : additions) {
            if (addition.getQualifiedName().equals(qualifiedName)) {
                return addition;
            }
        }
        return null;
    }

    /**
     * 检查一个类是否是一些类中其中一个的自雷
     * @param element 需要检查的类
     * @param superClasses 目标父类
     * @return 如果是则返回true
     */
    private boolean checkIsSubClassOf(Element element, String... superClasses) {
        Elements elementUtils = processingEnv.getElementUtils();
        Types typeUtils = processingEnv.getTypeUtils();
        for (String clazz : superClasses) {
            try {
                boolean isSubType = typeUtils.isSubtype(element.asType(), elementUtils.getTypeElement(clazz).asType());
                if (isSubType) return true;
            } catch (Throwable throwable) {
                logger.info(throwable.getMessage());
                continue;
            }
        }
        return false;
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
        annotations.add(Autowired.class);
        return annotations;
    }
}
