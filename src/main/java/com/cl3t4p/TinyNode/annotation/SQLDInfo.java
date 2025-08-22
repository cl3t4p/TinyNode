package com.cl3t4p.TinyNode.annotation;

import java.lang.annotation.*;

/** SQLDInfo - An annotation to provide metadata for SQL Mapping. */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SQLDInfo {

  String sql_name() default "";

  boolean ignore() default true;
}
