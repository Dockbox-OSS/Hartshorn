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

package org.dockbox.selene.server.minecraft;

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.domain.Identifiable;
import org.dockbox.selene.api.domain.tuple.Tristate;
import org.dockbox.selene.api.i18n.PermissionHolder;
import org.dockbox.selene.api.i18n.common.Language;
import org.dockbox.selene.api.i18n.common.ResourceEntry;
import org.dockbox.selene.api.i18n.permissions.Permission;
import org.dockbox.selene.api.i18n.permissions.PermissionContext;
import org.dockbox.selene.api.i18n.text.Text;
import org.dockbox.selene.commands.CommandInterface;
import org.dockbox.selene.commands.source.CommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class Console implements CommandSource, PermissionHolder, Identifiable, CommandInterface {

    @SuppressWarnings("ConstantDeclaredInAbstractClass")
    public static final UUID UNIQUE_ID = new UUID(0, 0);
    protected static Console instance;

    protected Console() {
        if (null != instance) return;
        instance = this;
    }

    public static Console getInstance() {
        if (null == instance) return Selene.context().get(Console.class);
        return instance;
    }

    @Override
    public Language getLanguage() {
        return Language.EN_US;
    }

    @Override
    public void setLanguage(Language language) {
        // Nothing happens
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
    public boolean hasPermission(@NotNull Permission permission) {
        return true;
    }

    @Override
    public boolean hasAnyPermission(@NotNull Permission @NotNull ... permissions) {
        return true;
    }

    @Override
    public boolean hasAllPermissions(@NotNull Permission @NotNull ... permissions) {
        return true;
    }

    @Override
    public void setPermission(@NotNull String permission, Tristate state) {}

    @Override
    public void setPermissions(Tristate state, @NotNull String @NotNull ... permissions) {}

    @Override
    public void setPermission(@NotNull Permission permission, Tristate state) {}

    @Override
    public void setPermissions(Tristate state, @NotNull Permission @NotNull ... permissions) {}

    @Override
    public void send(@NotNull ResourceEntry text) {
        Text formattedValue = text.translate().asText();
        this.send(formattedValue);
    }

    @Override
    public void sendWithPrefix(@NotNull ResourceEntry text) {
        Text formattedValue = text.translate().asText();
        this.sendWithPrefix(formattedValue);
    }

    @Override
    public UUID getUniqueId() {
        return UNIQUE_ID;
    }

    @Override
    public String getName() {
        return "Console";
    }

    @Override
    public PermissionContext activeContext() {
        // Console will always have all permissions, context is therefore global by default
        return PermissionContext.builder().build();
    }
}
