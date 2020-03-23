package com.darwinreforged.servermodifications.modules;

import org.spongepowered.api.plugin.Dependency;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DarwinModule {
    String id();
    String name() default "";
    String version() default "";
    Dependency[] dependencies() default {};
    String description() default "";
    String url() default "";
    String[] authors() default {};
}
