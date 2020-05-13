package com.darwinreforged.server.core.commands.registrations;

import com.darwinreforged.server.core.commands.annotations.Command;
import com.darwinreforged.server.core.resources.Permissions;

/**
 The type Command registration.
 */
public abstract class CommandRegistration {
    private final String primaryAlias;
    private final String[] aliases;
    private final Permissions[] permissions;
    private final Command command;

    /**
     Instantiates a new Command registration.

     @param primaryAlias
     the primary alias
     @param aliases
     the aliases
     @param permissions
     the permissions
     @param command
     the command
     */
    public CommandRegistration(String primaryAlias, String[] aliases, Permissions[] permissions, Command command) {
        this.primaryAlias = primaryAlias;
        this.aliases = aliases;
        this.permissions = permissions;
        this.command = command;
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
    public Permissions[] getPermissions() {
        return permissions;
    }

    /**
     Gets command.

     @return the command
     */
    public Command getCommand() {
        return command;
    }
}
