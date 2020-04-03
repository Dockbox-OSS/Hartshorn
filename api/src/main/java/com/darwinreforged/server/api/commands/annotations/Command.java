package com.darwinreforged.server.api.commands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    boolean isParent() default false;
    String command() default "$none";
    String permission() default "$none";
    CommandArgument[] arguments() default {};
    String description() default "None";
}
