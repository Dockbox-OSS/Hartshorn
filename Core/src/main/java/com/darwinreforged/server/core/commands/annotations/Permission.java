package com.darwinreforged.server.core.commands.annotations;

import com.darwinreforged.server.core.resources.Permissions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 The interface Permission.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {
    /**
     Value permissions [ ].

     @return the permissions [ ]
     */
    Permissions[] value();
}
