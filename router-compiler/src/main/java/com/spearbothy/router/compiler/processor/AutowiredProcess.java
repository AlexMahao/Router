package com.spearbothy.router.compiler.processor;

import com.google.auto.service.AutoService;
import com.spearbothy.router.annotation.Autowired;
import com.spearbothy.router.compiler.entity.AutowiredDetail;
import com.spearbothy.router.compiler.util.Constants;
import com.spearbothy.router.compiler.util.Logger;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static javax.lang.model.element.Modifier.PRIVATE;

/**
 * 处理自动注入
 *
 * @author mahao
 * @date 2018/7/19 下午3:02
 * @email zziamahao@163.com
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AutowiredProcess extends AbstractProcessor {


    private Filer filer;
    private Logger logger;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler(); // 生成class
        logger = new Logger(processingEnv.getMessager());
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<>(Collections.singletonList(Autowired.class.getCanonicalName()));
    }


    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> autowireElements = roundEnvironment.getElementsAnnotatedWith(Autowired.class);

        List<AutowiredDetail> autowiredDetails = new ArrayList<>();

        for (Element element : autowireElements) {
            // 检查element的类型
            if (!checkAnnotationValid(element, Autowired.class)) {
                return false;
            }
            VariableElement variableElement = (VariableElement) element;
            TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
            // 全路径类名
            String qualifiedName = typeElement.getQualifiedName().toString();
            logger.info("autowiredDetail:" + qualifiedName);
            if (checkIsSubClassOf(typeElement, Constants.CLASS_ACTIVITY, Constants.CLASS_FRAGMENT_ACTIVITY)) {
                logger.info("addAutowiredDetail:" + qualifiedName);
                addAutowiredDetail(autowiredDetails, qualifiedName, variableElement);
            }

        }

        logger.info("autowiredDetail:" + autowiredDetails.toString());

        ClassName bundleClass = ClassName.get("android.os", "Bundle");

        for (AutowiredDetail detail : autowiredDetails) {
            // 生成java文件
            // 构造 onSaveInstance(Activity instance, Bundle outState){
            //    outState.putXXX(name, instance.name);
            // }
            MethodSpec.Builder saveInstanceBuilder = MethodSpec.methodBuilder("onSaveInstanceState")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(void.class)
                    .addParameter(ClassName.bestGuess(detail.getQualifiedName()), "instance")
                    .addParameter(bundleClass, "outState");

            for (Element field : detail.getElements()) {
                BundleStateHelper.statementSaveValueIntoBundle(saveInstanceBuilder, field, "instance", "outState");
            }
            MethodSpec saveInstanceMethod = saveInstanceBuilder.build();

            // 构造 onRestoreInstanceState(Activity instance, Bundle outState){
            //    instance.name = outState.getXXX(name);
            // }
            MethodSpec.Builder restoreBuilder = MethodSpec.methodBuilder("onRestoreInstanceState")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(void.class)
                    .addParameter(ClassName.bestGuess(detail.getQualifiedName()), "instance")
                    .addParameter(bundleClass, "outState");
            for (Element field : detail.getElements()) {
                BundleStateHelper.statementGetValueFromBundle(restoreBuilder, field, "instance", "outState");
            }
            MethodSpec restoreMethod = restoreBuilder.build();
            //生成类
            TypeSpec saveStateClass = TypeSpec.classBuilder(detail.getSimpleName() + Constants.GENERATED_FILE_SUFFIX)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addMethod(saveInstanceMethod)
                    .addMethod(restoreMethod)
                    .build();


            JavaFile javaFile = JavaFile.builder(detail.getPackageName(), saveStateClass)
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
        return true;
    }

    /**
     * 检查查找的元素是否合法
     */
    private boolean checkAnnotationValid(Element annotatedElement, Class clazz) {
        if (annotatedElement.getKind() != ElementKind.FIELD) {
            logger.error(clazz.getSimpleName() + " must be declared on field.");
            return false;
        }
        if (annotatedElement.getModifiers().contains(PRIVATE)) {
            logger.error(clazz.getSimpleName() + " must can not be private.");
            return false;
        }
        return true;
    }

    private void addAutowiredDetail(List<AutowiredDetail> autowiredDetails, String qualifiedName, VariableElement element) {
        AutowiredDetail detail = null;
        for (AutowiredDetail data : autowiredDetails) {
            if (data.getQualifiedName().equals(qualifiedName)) {
                detail = data;
                break;
            }
        }
        if (detail == null) {
            detail = new AutowiredDetail();
            detail.setQualifiedName(qualifiedName);
            autowiredDetails.add(detail);
        }
        detail.addElements(element);
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
}
