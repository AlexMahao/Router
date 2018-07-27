package com.spearbothy.router.plugin

import groovy.util.slurpersupport.GPathResult
import org.gradle.api.Plugin
import org.gradle.api.Project

class RoutePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
//        project.afterEvaluate {
//            String packageName;
//            project.android.applicationVariants.each { variant ->
//                packageName = getPackageName(variant);
////                variant.javaCom
////                variant.javaCompile.options.
//                Map<String, String> arguments = variant.javaCompileOptions.annotationProcessorOptions.arguments
////                arguments.put("moduleName", packageName);
//                System.out.println("variant arguments:" + arguments);
//            }
//            // 添加包名
//            Map<String, String> arguments = project.android.defaultConfig.javaCompileOptions.annotationProcessorOptions.arguments;
//
//            System.out.println(arguments)
//
//            arguments.put("moduleName", packageName);
//
//            System.out.println("------")
//            System.out.println(arguments)
//
//
//            System.out.println("packageName " + packageName);
//
//        }
        project.dependencies {
            annotationProcessor 'com.spearbothy:router-compiler:0.0.1' // 代码生成
            api "com.spearbothy:router-api:0.0.1" // 公开api
            api 'com.spearbothy:router-annotation:0.0.3' // 注解
        }
        // 注册Transform
        project.android.registerTransform(new RouteAutowriedTransform(project))

        project.afterEvaluate {
            // up-to-date
            project.tasks.findByName("transformClassesWithRouteAutowiredForRelease").outputs.upToDateWhen { false }
        }
    }

    /**
     * 获取当前变体的包名 from butterknife
     * @return 包名
     */
    String getPackageName(def variant) {
        XmlSlurper slurper = new XmlSlurper(false, false)
        def list = new ArrayList<>();
        variant.sourceSets.each {
            list.add(it.manifestFile)
        }
        GPathResult result = slurper.parse(list[0])
        System.out.println(result.text())
        return result.getProperty("@package").toString()
    }
}
