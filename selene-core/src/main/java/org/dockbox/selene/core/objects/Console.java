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

package org.dockbox.selene.core.objects;

import org.dockbox.selene.core.command.source.CommandSource;
import org.dockbox.selene.core.i18n.common.ResourceEntry;
import org.dockbox.selene.core.i18n.permissions.AbstractPermission;
import org.dockbox.selene.core.objects.targets.PermissionHolder;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.text.Text;
import org.jetbrains.annotations.NotNull;

public abstract class Console implements CommandSource, PermissionHolder {

    protected static Console instance;

    protected Console() {
        if (null != instance) throw new IllegalStateException("Console has already been initialized!");
        instance = this;
    }

    public static Console getInstance() {
        if (null == instance) return Selene.provide(Console.class);
        return instance;
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return true;
    }

    @Override
    public boolean hasAnyPermission(@NotNull String @NotNull ... permissions) {
        return true;
    }

    @Override
    public boolean hasAllPermissions(@NotNull String @NotNull ... permissions) {
        return true;
    }

    @Override
    public boolean hasPermission(@NotNull AbstractPermission permission) {
        return true;
    }

    @Override
    public boolean hasAnyPermission(@NotNull AbstractPermission @NotNull ... permissions) {
        return true;
    }

    @Override
    public boolean hasAllPermissions(@NotNull AbstractPermission @NotNull ... permissions) {
        return true;
    }

    @Override
    public void setPermission(@NotNull String permission, boolean value) { }

    @Override
    public void setPermissions(boolean value, @NotNull String @NotNull ... permissions) { }

    @Override
    public void setPermission(@NotNull AbstractPermission permission, boolean value) { }

    @Override
    public void setPermissions(boolean value, @NotNull AbstractPermission @NotNull ... permissions) { }

    @Override
    public void send(@NotNull ResourceEntry text) {
        Text formattedValue = text.translate(Selene.getServer().getGlobalConfig().getDefaultLanguage()).asText();
        this.send(formattedValue);
    }

    @Override
    public void sendWithPrefix(@NotNull ResourceEntry text) {
        Text formattedValue = text.translate(Selene.getServer().getGlobalConfig().getDefaultLanguage()).asText();
        this.sendWithPrefix(formattedValue);
    }
}
