package com.pcpl.carbon.pcplsdk.Common.Utility;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class AppUtil {
    public static boolean isListEmpty(List list) {
        return list == null || list.isEmpty();
    }
    public static void traceMethodEntry() {
        StackTraceElement traceElement = Thread.currentThread().getStackTrace()[2];
        log.debug("Entering {}.{}()", traceElement.getClassName(),
                traceElement.getMethodName());
    }
    public static void traceMethodExit() {
//        log.debug("Exiting {}", Thread.currentThread().getStackTrace()[2].getMethodName());
        StackTraceElement traceElement = Thread.currentThread().getStackTrace()[2];
        log.debug("Exiting {}.{}()", traceElement.getClassName(),
                traceElement.getMethodName());
    }
}
