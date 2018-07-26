package com.spearbothy.router.compiler.processor;

import com.google.auto.service.AutoService;
import com.spearbothy.router.annotation.Autowired;
import com.spearbothy.router.annotation.Route;
import com.spearbothy.router.compiler.entity.Addition;
import com.spearbothy.router.compiler.entity.AutowiredField;
import com.spearbothy.router.compiler.entity.RouteClass;
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
            } catch (IOException e) {
                logger.error(e);
            }
            return true;
        }
        return false;
    }

    private void generatorRouteFile(List<Addition> additionList) throws IOException{
        ClassName InterfaceName = ClassName.get(Constants.LOADER_INTERFACE_PACKAGE, Constants.LOADER_INTERFACE_NAME);

        ClassName routeAddition = ClassName.get(Constants.ROUTER_PACKAGE + ".api.entity", "RouteAddition");

        ParameterizedTypeName routerMapType = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                routeAddition
        );

        ParameterSpec parameterSpec = ParameterSpec.builder(routerMapType, "root").build();
        MethodSpec.Builder loadIntoBuilder = MethodSpec.methodBuilder(Constants.LOADER_INTERFACE_LOADINTO)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(parameterSpec);

        loadIntoBuilder.addStatement("$T routeAddition = null", routeAddition);
        for (Addition addition : additionList) {
            RouteClass routeClass = addition.getRouteClass();
            if (routeClass != null) {
                loadIntoBuilder.addStatement("routeAddition = new $T($T.class, $S, $S)", routeAddition, routeClass.getClassName(), routeClass.getDesc(), routeClass.getVersion());
                for (AutowiredField field : addition.getAutowiredFields()){
                    loadIntoBuilder.addStatement("routeAddition.addAutowiredField($S, $S)", field.getFieldName(),field.getFieldType());
                }
                loadIntoBuilder.addStatement("root.put($S, routeAddition)", routeClass.getPath());
            }
        }
//
//        for (Map.Entry<TypeElement, Route> entry : routerMap.entrySet()) {
//            TypeElement element = entry.getKey();
//            Route value = entry.getValue();
//            loadIntoBuilder.addStatement("root.put($S, new $T($T.class, $S, $S))", value.path(), RouteEntity.class, ClassName.get(element), value.desc(), value.version());
//
//            // 输出路由清单
//            RouterDetail.Path path = new RouterDetail.Path(ClassName.get(element).packageName() + ClassName.get(element).simpleName());
//            path.setPath(value.path(), moduleName, Constants.ROUTER_PROTOCOL);
//            path.addParams("version", value.version());
//            path.setDesc(value.desc());
//            routerDetail.addPath(path);
//        }

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


    }

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
                BundleStateHelper.statementGetValueFromBundle(restoreBuilder, field.getFieldName(), field.getFieldType(), "instance", "outState");
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


    private void initAutowird(List<Addition> additionList, RoundEnvironment roundEnv) {
        Set<? extends Element> autowiredElements = roundEnv.getElementsAnnotatedWith(Autowired.class);
        if (autowiredElements != null && !autowiredElements.isEmpty()) {
            for (Element element : autowiredElements) {
                VariableElement variableElement = (VariableElement) element;
                TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
                // 全路径类名
                String qualifiedName = typeElement.getQualifiedName().toString();
                if (checkIsSubClassOf(typeElement, Constants.CLASS_ACTIVITY, Constants.CLASS_FRAGMENT_ACTIVITY)) {

                    Addition addition = findAddition(additionList, qualifiedName);
                    if (addition == null) {
                        addition = new Addition();
                        addition.setQualifiedName(qualifiedName);
                        additionList.add(addition);
                    }
                    AutowiredField field = new AutowiredField();
                    field.setFieldName(variableElement.getSimpleName().toString());
                    field.setFieldType(variableElement.asType().toString());
                    addition.addAutowiredField(field);
                }
            }
        }
    }


    public void initRoute(List<Addition> additions, RoundEnvironment roundEnv) {
        Set<? extends Element> routeElements = roundEnv.getElementsAnnotatedWith(Route.class);
        if (routeElements != null && !routeElements.isEmpty()) {
            for (Element element : routeElements) {
                // 检查element的类型
                TypeElement typeElement = (TypeElement) element;
                Route route = typeElement.getAnnotation(Route.class);
                String qualifiedName = typeElement.getQualifiedName().toString();
                Addition addition = findAddition(additions, qualifiedName);
                if (addition == null) {
                    addition = new Addition();
                    addition.setQualifiedName(qualifiedName);
                    additions.add(addition);
                }
                RouteClass routeClass = new RouteClass();
                routeClass.setClassName(ClassName.get(typeElement));
                routeClass.setDesc(route.desc());
                routeClass.setPath(route.path());
                routeClass.setVersion(route.version());
                addition.setRouteClass(routeClass);

            }
        }
    }

    public Addition findAddition(List<Addition> additions, String qualifiedName) {
        for (Addition addition : additions) {
            if (addition.getQualifiedName().equals(qualifiedName)) {
                return addition;
            }
        }
        return null;
    }

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
        annotations.add(Autowired.class);
        return annotations;
    }
}
