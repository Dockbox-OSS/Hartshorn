package com.darwinreforged.server.core.modules;

import com.darwinreforged.server.core.resources.Dependencies;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 The interface Module.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Module {
    /**
     Id string.

     @return the string
     */
    String id();

    /**
     Name string.

     @return the string
     */
    String name();

    /**
     Version string.

     @return the string
     */
    String version() default "unknown";

    /**
     Description string.

     @return the string
     */
    String description();

    /**
     Url string.

     @return the string
     */
    String url() default "none";

    /**
     Authors string [ ].

     @return the string [ ]
     */
    String[] authors();

    Dependencies[] dependencies() default {};
}
