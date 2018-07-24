package com.spearbothy.router.plugin

import com.android.build.api.transform.*
import com.google.common.collect.Sets
import javassist.ClassPath
import javassist.ClassPool
import javassist.CtClass
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

import java.lang.reflect.Constructor

class SaveStateTransform extends Transform {

    Project mProject

    SaveStateTransform(Project project) {
        mProject = project
    }

    /**
     * transform的名称
     * transformClassesWith + getName() + For + Debug或Release
     * @return
     */
    @Override
    String getName() {
        return "saveState"
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
        Logger.info("bootClassPath:" + mProject.android.bootClasspath[0].toString())

        classPool.appendClassPath(mProject.android.bootClasspath[0].toString())

        try {
            Class jarClassPathClazz = Class.forName("javassist.JarClassPath")
            Constructor constructor = jarClassPathClazz.getDeclaredConstructor(String.class)
            constructor.setAccessible(true)

            transformInvocation.inputs.each { input ->

                def subProjectInputs = []

                input.jarInputs.each { jarInput ->
                    Logger.info("jar input:" + jarInput.file.getAbsolutePath() + " jarInput.contentTypes" + jarInput.contentTypes + " jarInput.scope" + jarInput.scopes)

                    ClassPath clazzPath = (ClassPath) constructor.newInstance(jarInput.file.absolutePath)
                    classPath.add(clazzPath)
                    classPool.appendClassPath(clazzPath)

                    def jarName = jarInput.name
                    if (jarName.endsWith(".jar")) {
                        jarName = jarName.substring(0, jarName.length() - 4)
                    }
                    Logger.info("jar name:" + jarName)
                    if (jarName.startsWith(":")) {
                        // handle it later, after classpath set
                        def dest = transformInvocation.outputProvider.getContentLocation(jarName, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                        Logger.info("jar output path:" + dest.getAbsolutePath())
                        FileUtils.copyFile(jarInput.file, dest)
                    } else {
                        def dest = transformInvocation.outputProvider.getContentLocation(jarName, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                        Logger.info("jar output path:" + dest.getAbsolutePath())
                        FileUtils.copyFile(jarInput.file, dest)
                    }
                }

                /* // Handle library project jar here
                 subProjectInputs.each { jarInput ->

                     def jarName = jarInput.name
                     if (jarName.endsWith(".jar")) {
                         jarName = jarName.substring(0, jarName.length() - 4)
                     }

                     if (jarName.startsWith(":")) {
                         // sub project
                         File unzipDir = new File(
                                 jarInput.file.getParent(),
                                 jarName.replace(":", "") + "_unzip")
                         if (unzipDir.exists()) {
                             unzipDir.delete()
                         }
                         unzipDir.mkdirs()
                         Decompression.uncompress(jarInput.file, unzipDir)

                         File repackageFolder = new File(
                                 jarInput.file.getParent(),
                                 jarName.replace(":", "") + "_repackage"
                         )

                         FileUtils.copyDirectory(unzipDir, repackageFolder)

                         unzipDir.eachFileRecurse(FileType.FILES) { File it ->
                             checkAndTransformClass(classPool, it, repackageFolder)
                         }

                         // re-package the folder to jar
                         def dest = transformInvocation.outputProvider.getContentLocation(
                                 jarName, jarInput.contentTypes, jarInput.scopes, Format.JAR)

                         Compressor zc = new Compressor(dest.getAbsolutePath())
                         zc.compress(repackageFolder.getAbsolutePath())
                     }
                 }*/

                input.directoryInputs.each { dirInput ->
                    def outDir = transformInvocation.outputProvider.getContentLocation(dirInput.name, dirInput.contentTypes, dirInput.scopes, Format.DIRECTORY)
                    Logger.info("dirInput:" + dirInput.file.absolutePath);
                    // 获取到所有类
                    classPool.appendClassPath(dirInput.file.absolutePath)
                    // dirInput.file is like "build/intermediates/classes/debug"
                    int pathBitLen = dirInput.file.toString().length()
                    Logger.info("pathBitLen:" + dirInput.file.toString());
                    def callback = { File it ->
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
                        Logger.info("path:" + path);
                    }

                    if (dirInput.changedFiles == null || dirInput.changedFiles.isEmpty()) {
                        // 有文件改变，只需要改变新的文件 instantrun
                        dirInput.file.traverse(callback)
                    } else {
                        // 没有文件改变
                        dirInput.changedFiles.keySet().each(callback)
                    }
                }
            }
        } finally {
            // release File Handlers in ClassPool
            classPath.each { it ->
                classPool.removeClassPath(it)
            }
        }

    }


    boolean checkAndTransformClass(ClassPool classPool, File file, File dest) {

        CtClass fragmentActivityCtClass
        CtClass v4FragmentCtClass

        try {
            fragmentActivityCtClass = classPool.get("android.support.v4.app.FragmentActivity")
            v4FragmentCtClass = classPool.get("android.support.v4.app.Fragment")
        } catch (Throwable t) {
            // It's ok
        }

        CtClass activityCtClass = classPool.get("android.app.Activity")
        CtClass fragmentCtClass = classPool.get("android.app.Fragment")
        CtClass viewCtClass = classPool.get("android.view.View")

        classPool.importPackage("android.os")
        classPool.importPackage("android.util")

        if (!file.name.endsWith("class")) {
            return false
        }

        CtClass ctClass
        try {
            ctClass = classPool.makeClass(new FileInputStream(file))
        } catch (Throwable throwable) {
            mProject.logger.error("Parsing class file ${file.getAbsolutePath()} fail.", throwable)
            return false
        }
        // Support Activity and AppCompatActivity
        boolean handled
        if (ctClass.subclassOf(activityCtClass) || (fragmentActivityCtClass != null && ctClass.subclassOf(fragmentActivityCtClass))) {
//            mProject.logger.info("save-state checking activity class:" + ctClass.getName())

            Logger.info("saveState inject " + ctClass.getName());

            ActivitySaveStateTransform transform = new ActivitySaveStateTransform(mProject, ctClass, classPool)
            transform.handleActivitySaveState()
            handled = true
        }
        // 暂时不起作用
        /*else if (ctClass.subclassOf(fragmentCtClass) || (v4FragmentCtClass != null && ctClass.subclassOf(v4FragmentCtClass))) {
//            mProject.logger.info("save-state checking fragment class:" + ctClass.getName())
            FragmentSaveStateTransform transform = new FragmentSaveStateTransform(ctClass, classPool, mProject)
            transform.handleFragmentSaveState()
            handled = true
        } else if (ctClass.subclassOf(viewCtClass)) {
//            mProject.logger.info("save-state checking view class:" + ctClass.getName())
            ViewSaveStateTransform transform = new ViewSaveStateTransform(ctClass, classPool, mProject)
            transform.handleViewSaveState()
            handled = true
        }*/
        if (handled) {
            ctClass.writeFile(dest.absolutePath)
            ctClass.detach()
        }
        return handled
    }
}