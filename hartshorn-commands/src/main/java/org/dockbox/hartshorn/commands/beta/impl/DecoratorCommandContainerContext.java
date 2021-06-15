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

package org.dockbox.hartshorn.commands.beta.impl;

import org.dockbox.hartshorn.api.i18n.permissions.Permission;
import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.commands.beta.api.CommandContainerContext;
import org.dockbox.hartshorn.di.context.DefaultContext;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.time.temporal.ChronoUnit;
import java.util.List;

public class DecoratorCommandContainerContext extends DefaultContext implements CommandContainerContext {

    private final Command command;

    public DecoratorCommandContainerContext(Command command) {
        this.command = command;
    }

    @Override
    public List<String> aliases() {
        return HartshornUtils.asUnmodifiableList(this.command.value());
    }

    @Override
    public String arguments() {
        return this.command.arguments();
    }

    @Override
    public Permission permission() {
        return Permission.of(this.command.permission());
    }

    @Override
    public long cooldown() {
        return this.command.cooldownDuration();
    }

    @Override
    public ChronoUnit cooldownUnit() {
        return this.command.cooldownUnit();
    }

    @Override
    public boolean inherited() {
        return this.command.inherit();
    }

    @Override
    public boolean extended() {
        return this.command.extend();
    }

    @Override
    public boolean confirmation() {
        return this.command.confirm();
    }

    @Override
    public Class<?> parent() {
        return this.command.parent();
    }
}
