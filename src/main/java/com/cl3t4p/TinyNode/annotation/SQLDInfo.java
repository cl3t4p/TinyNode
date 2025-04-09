package com.cl3t4p.TinyNode.annotation;

import java.lang.annotation.*;


/**
 * This annotation is used to represent a table name in the database or ignore a field.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SQLDInfo {

    String sql_name() default "";

    boolean ignore() default false;
}