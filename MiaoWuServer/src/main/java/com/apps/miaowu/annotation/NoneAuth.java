package com.apps.miaowu.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;



/**
 * Methods with this annotation skip the login check
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface NoneAuth{

}