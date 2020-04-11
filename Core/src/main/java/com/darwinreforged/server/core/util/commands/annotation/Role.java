package com.darwinreforged.server.core.util.commands.annotation;


public @interface Role {

    String value();

    boolean permit() default true;
}
