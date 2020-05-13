package com.darwinreforged.server.core.commands.registrations;

import com.darwinreforged.server.core.commands.annotations.Command;
import com.darwinreforged.server.core.resources.Permissions;

public abstract class CommandRegistration {
    private final String primaryAlias;
    private final String[] aliases;
    private final Permissions[] permissions;
    private final Command command;

    public CommandRegistration(String primaryAlias, String[] aliases, Permissions[] permissions, Command command) {
        this.primaryAlias = primaryAlias;
        this.aliases = aliases;
        this.permissions = permissions;
        this.command = command;
    }

    public String getPrimaryAlias() {
        return primaryAlias;
    }

    public String[] getAliases() {
        return aliases;
    }

    public Permissions[] getPermissions() {
        return permissions;
    }

    public Command getCommand() {
        return command;
    }
}
