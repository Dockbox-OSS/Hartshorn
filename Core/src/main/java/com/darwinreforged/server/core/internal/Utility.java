package com.darwinreforged.server.core.internal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 The interface Abstract utility.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Utility {
    /**
     Value string.

     @return the string
     */
    String value();
}
