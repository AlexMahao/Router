package com.spearbothy.router.plugin

import com.android.build.api.transform.*
import com.google.common.collect.Sets
import com.spearbothy.router.annotation.Autowired
import javassist.*
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.gradle.internal.impldep.com.esotericsoftware.minlog.Log

import java.lang.reflect.Constructor

class RouteAutowriedTransform extends Transform {

    Project mProject

    RouteAutowriedTransform(Project project) {
        mProject = project
    }

    /**
     * transform的名称
     * transformClassesWith + getName() + For + Debug或Release
     */
    @Override
    String getName() {
        return "routeAutowired"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return Collections.singleton(QualifiedContent.DefaultContentType.CLASSES)
    }

    @Override
    Set<QualifiedContent.Scope> getScopes() {
        if (mProject.plugins.hasPlugin("com.android.application")) {
            return Sets.immutableEnumSet(
                    QualifiedContent.Scope.PROJECT,
                    QualifiedContent.Scope.SUB_PROJECTS,
                    QualifiedContent.Scope.EXTERNAL_LIBRARIES)
        } else if (mProject.plugins.hasPlugin("com.android.library")) {
            return Sets.immutableEnumSet(
                    QualifiedContent.Scope.PROJECT)
        } else {
            return Collections.emptySet()
        }
    }

    @Override
    boolean isIncremental() {
        // 增量编译  instant run
        return true
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        ClassPool classPool = ClassPool.getDefault()
        def classPath = []
        // 环境变量
        Logger.info("bootClassPath:" + mProject.android.bootClasspath[0].toString())

        classPool.appendClassPath(mProject.android.bootClasspath[0].toString())

        Class jarClassPathClazz = Class.forName("javassist.JarClassPath")
        Constructor constructor = jarClassPathClazz.getDeclaredConstructor(String.class)
        constructor.setAccessible(true)

        transformInvocation.inputs.each { input ->
            // 对jar包不做任何处理
            input.jarInputs.each { jarInput ->
                // 对于jar包不做任何处理
                Logger.info("jar not process =========================");
                Logger.info("jar.name:" + jarInput.name + "jar input:" + jarInput.file.getAbsolutePath() + " jarInput.contentTypes" + jarInput.contentTypes + " jarInput.scope" + jarInput.scopes)
                // 添加jar包，否则调用ctClass.subClass（）时会因找不到父类而返回false
                ClassPath clazzPath = (ClassPath) constructor.newInstance(jarInput.file.absolutePath)
                classPool.appendClassPath(clazzPath)
                classPath.add(clazzPath)

                def dest = transformInvocation.outputProvider.getContentLocation(jarInput.name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                FileUtils.copyFile(jarInput.file, dest)
                Logger.info("jar output:" + dest.absolutePath);
            }
        }

        transformInvocation.inputs.each { input->
            input.directoryInputs.each { dirInput ->
                def outDir = transformInvocation.outputProvider.getContentLocation(dirInput.name, dirInput.contentTypes, dirInput.scopes, Format.DIRECTORY)

                // 获取到所有类
                classPool.appendClassPath(dirInput.file.absolutePath)
                int pathBitLen = dirInput.file.toString().length()
                Logger.info("dirInput:" + dirInput.file.absolutePath);
                def callback = { File it ->
                    // 截取前置目录
                    def path = "${it.toString().substring(pathBitLen)}"
                    if (it.isDirectory()) {
                        new File(outDir, path).mkdirs()
                    } else {
                        // 文件路径
                        boolean handled = checkAndTransformClass(classPool, it, outDir)
                        if (!handled) {
                            // 如果不支持，手动拷贝到制定目录
                            new File(outDir, path).bytes = it.bytes
                        }
                    }
                }

                if (dirInput.changedFiles == null || dirInput.changedFiles.isEmpty()) {
                    // 没有文件改变
                    dirInput.file.traverse(callback)
                } else {
                    // 有文件改变，只需要改变新的文件 instantrun
                    dirInput.changedFiles.keySet().each(callback)
                }
            }
        }

        classPath.each { it -> classPool.removeClassPath(it) }
    }


    boolean checkAndTransformClass(ClassPool classPool, File file, File dest) {

//        CtClass fragmentActivityCtClass = classPool.get("android.support.v4.app.FragmentActivity")
        CtClass activityCtClass = classPool.get("android.app.Activity")

        classPool.importPackage("android.os")
        classPool.importPackage("android.util")

        if (!file.name.endsWith("class")) {
            return false
        }

        CtClass ctClass
        try {
            ctClass = classPool.makeClass(new FileInputStream(file))
        } catch (Throwable throwable) {
            Logger.error("Parsing class file ${file.getAbsolutePath()} fail.", throwable)
            return false
        }
        if (ctClass.subclassOf(activityCtClass)) {
            Logger.info("route-save-params checking activity class:" + ctClass.getName())
            Logger.info("route-save-params inject " + ctClass.getName())
            handleActivitySaveState(mProject, ctClass, classPool)
            ctClass.writeFile(dest.absolutePath)
            ctClass.detach()
            return true
        }
        return false
    }


    void handleActivitySaveState(Project project, CtClass ctClass, ClassPool classPool) {

        CtClass bundleCtClass = classPool.get("android.os.Bundle")

        // 寻找onSaveInstanceState方法
        CtMethod saveCtMethod = ctClass.declaredMethods.find {
            it.name == "onSaveInstanceState" && it.parameterTypes == [bundleCtClass] as CtClass[]
        }
        // 寻找onCreate方法
        CtMethod restoreCtMethod = ctClass.declaredMethods.find {
            it.name == "onCreate" && it.parameterTypes == [bundleCtClass] as CtClass[]
        }

        // 是否可用
        CtField enableCtField = ctClass.declaredFields.find {
            it.name == Constant.ENABLE_SAVE_STATE && it.getType().name == "boolean"
        }

        def list = []

        ctClass.declaredFields.each { field ->
            if (field.getAnnotation(Autowired.class) != null) {
                Logger.info("field ${field.name} is Autowired annotated! ")
                list.add(field)
            }
        }
        if (list.size() == 0) {
            if (enableCtField != null) {
                // 原来有参数，现在没有了，不需要再查找
                ctClass.removeField(enableCtField)
                ctClass.addField(generateEnabledField(ctClass, classPool), CtField.Initializer.constant(false))
                Logger.info("${ctClass.simpleName} not save!")
            }
        } else {
            if (enableCtField == null) {
                Logger.info("${ctClass.simpleName} save !")
                // 原来没有需要自动恢复的变量，现在出现了需要自动恢复的变量
                ctClass.addField(generateEnabledField(ctClass, classPool), CtField.Initializer.constant(true))

                if (saveCtMethod == null) {
                    Logger.info("${ctClass.simpleName}  add onSaveInstance method")
                    // 原来的 Activity 没有 saveInstanceState 方法
                    saveCtMethod = CtNewMethod.make(generateActivitySaveMethod(ctClass.name + Constant.GENERATED_FILE_SUFFIX), ctClass)
                    ctClass.addMethod(saveCtMethod)
                } else {
                    Logger.info("${ctClass.simpleName}  onSaveInstance exist, insert before")
                    // 原来的 Activity 有 saveInstanceState 方法
                    saveCtMethod.insertBefore("${ctClass.name}${Constant.GENERATED_FILE_SUFFIX}.onSaveInstanceState(this, \$1);")
                }

                if (restoreCtMethod == null) {
                    Logger.info("${ctClass.simpleName}  add onCreate method")
                    // 原来的 Activity 没有 onCreate 方法
                    restoreCtMethod = CtNewMethod.make(generateActivityRestoreMethod(ctClass.name + Constant.GENERATED_FILE_SUFFIX), ctClass)
                    ctClass.addMethod(restoreCtMethod)
                } else {
                    Logger.info("${ctClass.simpleName}  onCreate exist, insert before")
                    // 原来的 Activity 有 onCreate 方法
                    restoreCtMethod.insertBefore("if ($Constant.ENABLE_SAVE_STATE){ if(\$1 != null) { ${ctClass.name}${Constant.GENERATED_FILE_SUFFIX}.onRestoreInstanceState(this, \$1);} else { ${ctClass.name}${Constant.GENERATED_FILE_SUFFIX}.onRestoreInstanceState(this, getIntent().getBundleExtra(\"router_bundle\"));}}")
                }
            }
        }
    }

    CtField generateEnabledField(CtClass ctClass, ClassPool classPool) {
        CtField ctField = new CtField(
                classPool.get("boolean"), Constant.ENABLE_SAVE_STATE, ctClass)
        ctField.setModifiers(Modifier.PRIVATE | Modifier.STATIC)
        return ctField
    }

    // Activity onCreate 不存在的情况下创建 onCreate 方法
    String generateActivityRestoreMethod(String delegatedName) {
        return "protected void onCreate(Bundle savedInstanceState) {\n" +
                "if ($Constant.ENABLE_SAVE_STATE) { " +
                "\tif(saveInstance == null) { " +
                "\t\t${delegatedName}.onRestoreInstanceState(this, getIntent().getBundleExtra(\"router_bundle\")); " +
                "\t} else { " +
                "\t\t${delegatedName}.onRestoreInstanceState(this, savedInstanceState); " +
                "\t}" +
                "}" + "\n" +
                "super.onCreate(savedInstanceState);\n" +
                "}"
    }

    // Activity onSaveInstanceState 不存在的情况下创建 onSaveInstanceState
    String generateActivitySaveMethod(String delegatedName) {
        return "protected void onSaveInstanceState(Bundle outState) {\n" +
                "${delegatedName}.onSaveInstanceState(this, outState);" + "\n" +
                "super.onSaveInstanceState(outState);\n" +
                "}"
    }
}