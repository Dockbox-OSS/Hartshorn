package com.darwinreforged.server.core.commands.annotations;

import com.darwinreforged.server.core.resources.Permissions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 The permission annotation to add permissions to a Command method or type.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {
    /**
     Indicates which permissions should be used for a method or type
     annotated with {@link Command}. Uses the {@link Permissions} type
     to allow for configurable permissions.

     @return the permissions
     */
    Permissions[] value();
}
