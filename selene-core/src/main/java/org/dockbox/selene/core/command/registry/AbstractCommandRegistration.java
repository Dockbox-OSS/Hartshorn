/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core.command.registry;

import org.dockbox.selene.core.annotations.command.Command;
import org.dockbox.selene.core.i18n.permissions.AbstractPermission;

public class AbstractCommandRegistration<T> {

    private final String primaryAlias;
    private final String[] aliases;
    private final AbstractPermission permission;
    private final Command command;
    private T sourceInstance;

    public AbstractCommandRegistration(String primaryAlias, String[] aliases, AbstractPermission permission, Command command, T sourceInstance) {
        this.primaryAlias = primaryAlias;
        this.aliases = aliases;
        this.permission = permission;
        this.command = command;
        this.sourceInstance = sourceInstance;
    }

    public String getPrimaryAlias() {
        return this.primaryAlias;
    }

    public String[] getAliases() {
        return this.aliases;
    }

    public AbstractPermission getPermission() {
        return this.permission;
    }

    public Command getCommand() {
        return this.command;
    }

    public T getSourceInstance() {
        return this.sourceInstance;
    }

    public void setSourceInstance(T sourceInstance) {
        this.sourceInstance = sourceInstance;
    }
}
