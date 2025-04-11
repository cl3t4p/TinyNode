package com.cl3t4p.TinyNode.tools;

import org.slf4j.Logger;
import org.slf4j.simple.SimpleLoggerFactory;

public class LoggerFactory {

  public static Logger getLogger(Class<?> clazz) {
    return new SimpleLoggerFactory().getLogger(clazz.getSimpleName());
  }
}
