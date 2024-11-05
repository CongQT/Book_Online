package com.example.bookreadingonline.util;

import org.slf4j.MDC;

public class MyLogUtils {

  public static String getTraceId() {
    return MDC.get("traceId");
  }

}
