package com.darwinreforged.server.core.init;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 The interface Utility implementation.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface UtilityImplementation {
    /**
     Value class.

     @return the class
     */
    Class<?> value();
}
