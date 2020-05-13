package com.darwinreforged.server.core.commands.registrations;

import com.darwinreforged.server.core.commands.annotations.Command;
import com.darwinreforged.server.core.resources.Permissions;

import java.lang.reflect.Method;

/**
 The type Single method registration.
 */
public final class SingleMethodRegistration extends CommandRegistration {

    private final Method method;

    /**
     Instantiates a new Single method registration.

     @param primaryAlias
     the primary alias
     @param aliases
     the aliases
     @param command
     the command
     @param method
     the method
     @param permissions
     the permissions
     */
    public SingleMethodRegistration(String primaryAlias, String[] aliases, Command command, Method method, Permissions[] permissions) {
        super(primaryAlias, aliases, permissions, command);
        this.method = method;
    }

    /**
     Gets method.

     @return the method
     */
    public Method getMethod() {
        return method;
    }
}
