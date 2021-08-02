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

package org.dockbox.hartshorn.commands;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.Identifiable;
import org.dockbox.hartshorn.api.domain.tuple.Tristate;
import org.dockbox.hartshorn.i18n.PermissionHolder;
import org.dockbox.hartshorn.i18n.common.Language;
import org.dockbox.hartshorn.i18n.common.ResourceEntry;
import org.dockbox.hartshorn.i18n.permissions.Permission;
import org.dockbox.hartshorn.i18n.permissions.PermissionContext;
import org.dockbox.hartshorn.i18n.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class SystemSubject implements CommandSource, PermissionHolder, Identifiable {

    @SuppressWarnings("ConstantDeclaredInAbstractClass")
    public static final UUID UNIQUE_ID = new UUID(0, 0);
    protected static SystemSubject instance;

    protected SystemSubject() {
        if (null != instance) return;
        instance = this;
    }

    public static SystemSubject instance() {
        if (null == instance) return Hartshorn.context().get(SystemSubject.class);
        return instance;
    }

    @Override
    public Language language() {
        return Language.EN_US;
    }

    @Override
    public void language(Language language) {
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
    public void permission(@NotNull String permission, Tristate state) {}

    @Override
    public void permissions(Tristate state, @NotNull String @NotNull ... permissions) {}

    @Override
    public void permission(@NotNull Permission permission, Tristate state) {}

    @Override
    public void permissions(Tristate state, @NotNull Permission @NotNull ... permissions) {}

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
    public UUID uniqueId() {
        return UNIQUE_ID;
    }

    @Override
    public String name() {
        return "System";
    }

    @Override
    public PermissionContext activeContext() {
        // System will always have all permissions, context is therefore global by default
        return PermissionContext.builder().build();
    }
}
