package com.darwinreforged.server.core.commands.annotation;


public @interface Role {

    String value();

    boolean permit() default true;
}
