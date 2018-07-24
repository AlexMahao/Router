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
//                Map<String, String> arguments = variant.javaCompileOptions.annotationProcessorOptions.arguments
//                arguments.put("moduleName", packageName);
//                System.out.println("arguments:" + arguments);
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
            annotationProcessor 'com.spearbothy:router-compiler:0.0.1'
            implementation 'com.spearbothy:router-annotation:0.0.1'
        }
    }

    String getPackageName(def variant) {
        XmlSlurper slurper = new XmlSlurper(false, false)
        def list = new ArrayList<>();
        variant.sourceSets.each {
            list.add(it.manifestFile)
        }
//        System.out.println(list[0]);
        // According to the documentation, the earlier files in the list are meant to be overridden by the later ones.
        // So the first file in the sourceSets list should be main.
        GPathResult result = slurper.parse(list[0])
        System.out.println(result.text())
        return result.getProperty("@package").toString()
//        return "";
    }
}
