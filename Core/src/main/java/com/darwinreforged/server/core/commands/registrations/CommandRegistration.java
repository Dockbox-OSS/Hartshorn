package com.darwinreforged.server.core.commands.registrations;

import com.darwinreforged.server.core.commands.annotations.Command;
import com.darwinreforged.server.core.resources.Permissions;

import java.util.Optional;

/**
 The type Command registration.
 */
public abstract class CommandRegistration {
    private final String primaryAlias;
    private final String[] aliases;
    private final Permissions permission;
    private final Command command;
    private Object sourceInstance;

    /**
     Instantiates a new Command registration.

     @param primaryAlias
     the primary alias
     @param aliases
     the aliases
     @param permission
     the permissions
     @param command
     the command
     */
    public CommandRegistration(String primaryAlias, String[] aliases, Permissions permission, Command command) {
        this.primaryAlias = primaryAlias;
        this.aliases = aliases;
        this.permission = permission;
        this.command = command;
    }

    public Optional<?> getSourceInstance() {
        return Optional.ofNullable(sourceInstance);
    }

    public void setSourceInstance(Object sourceInstance) {
        this.sourceInstance = sourceInstance;
    }

    /**
     Gets primary alias.

     @return the primary alias
     */
    public String getPrimaryAlias() {
        return primaryAlias;
    }

    /**
     Get aliases string [ ].

     @return the string [ ]
     */
    public String[] getAliases() {
        return aliases;
    }

    /**
     Get permissions permissions [ ].

     @return the permissions [ ]
     */
    public Permissions getPermission() {
        return permission;
    }

    /**
     Gets command.

     @return the command
     */
    public Command getCommand() {
        return command;
    }
}
