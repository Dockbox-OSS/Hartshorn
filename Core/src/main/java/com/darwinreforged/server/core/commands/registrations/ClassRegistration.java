package com.darwinreforged.server.core.commands.registrations;

import com.darwinreforged.server.core.commands.annotations.Command;
import com.darwinreforged.server.core.resources.Permissions;

/**
 The type Class registration.
 */
public final class ClassRegistration extends CommandRegistration {

    private final Class<?> clazz;
    private final SingleMethodRegistration[] subcommands;

    /**
     Instantiates a new Class registration.

     @param primaryAlias
     the primary alias
     @param aliases
     the aliases
     @param permissions
     the permissions
     @param command
     the command
     @param clazz
     the clazz
     @param subcommands
     the subcommands
     */
    public ClassRegistration(String primaryAlias, String[] aliases, Permissions permissions, Command command, Class<?> clazz, SingleMethodRegistration[] subcommands) {
        super(primaryAlias, aliases, permissions, command);
        this.clazz = clazz;
        this.subcommands = subcommands;
    }

    /**
     Gets clazz.

     @return the clazz
     */
    public Class<?> getClazz() {
        return clazz;
    }

    /**
     Get subcommands single method registration [ ].

     @return the single method registration [ ]
     */
    public SingleMethodRegistration[] getSubcommands() {
        return subcommands;
    }
}
