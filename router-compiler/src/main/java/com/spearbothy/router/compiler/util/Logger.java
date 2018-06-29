package com.spearbothy.router.compiler.util;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;


/**
 * processor logger info
 */
public class Logger {

    private Messager messager;

    public Logger(Messager messager) {
        this.messager = messager;
    }

    public void info(CharSequence info) {
        messager.printMessage(Diagnostic.Kind.NOTE, info);
    }

    public void infoLine(String title) {
        info(String.format("========================== %s ==========================", title));
    }

    public void error(String message) {
        messager.printMessage(Diagnostic.Kind.ERROR, message);
    }

    public void error(Throwable error) {
        messager.printMessage(Diagnostic.Kind.ERROR, "An exception is encountered, [" + error.getMessage() + "]" + "\n" + formatStackTrace(error.getStackTrace()));
    }

    private String formatStackTrace(StackTraceElement[] stackTrace) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : stackTrace) {
            sb.append("    at ").append(element.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

}
