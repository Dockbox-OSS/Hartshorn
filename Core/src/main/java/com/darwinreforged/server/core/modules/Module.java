package com.darwinreforged.server.core.modules;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Module {
    String id();

    String name();

    String version() default "InDev";

    String description();

    String url() default "none";

    String[] authors();

    String source() default "Unknown";
}
