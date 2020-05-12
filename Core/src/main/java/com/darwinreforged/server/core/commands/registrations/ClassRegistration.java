package com.darwinreforged.server.core.commands.registrations;

import com.darwinreforged.server.core.commands.annotations.Command;
import com.darwinreforged.server.core.resources.Permissions;

public final class ClassRegistration extends CommandRegistration {

    private final Class<?> clazz;
    private final SingleMethodRegistration[] subcommands;

    public ClassRegistration(String primaryAlias, String[] aliases, Permissions[] permissions, Command command, Class<?> clazz, SingleMethodRegistration[] subcommands) {
        super(primaryAlias, aliases, permissions, command);
        this.clazz = clazz;
        this.subcommands = subcommands;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public SingleMethodRegistration[] getSubcommands() {
        return subcommands;
    }
}
