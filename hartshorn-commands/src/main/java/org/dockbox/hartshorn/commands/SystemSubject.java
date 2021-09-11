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

import org.dockbox.hartshorn.api.domain.Identifiable;
import org.dockbox.hartshorn.api.domain.tuple.Tristate;
import org.dockbox.hartshorn.api.exceptions.Except;
import org.dockbox.hartshorn.commands.exceptions.ParsingException;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.i18n.PermissionHolder;
import org.dockbox.hartshorn.i18n.common.Language;
import org.dockbox.hartshorn.i18n.common.ResourceEntry;
import org.dockbox.hartshorn.i18n.permissions.Permission;
import org.dockbox.hartshorn.i18n.permissions.PermissionContext;
import org.dockbox.hartshorn.i18n.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public abstract class SystemSubject implements CommandSource, PermissionHolder, Identifiable {

    @Inject
    private ApplicationContext context;

    @SuppressWarnings("ConstantDeclaredInAbstractClass")
    public static final UUID UNIQUE_ID = new UUID(0, 0);

    public static SystemSubject instance(final ApplicationContext context) {
        return context.get(SystemSubject.class);
    }

    @Override
    public Language language() {
        return Language.EN_US;
    }

    @Override
    public void language(final Language language) {
        // Nothing happens
    }

    @Override
    public void send(@NotNull final ResourceEntry text) {
        final Text formattedValue = text.translate().asText();
        this.send(formattedValue);
    }

    @Override
    public void sendWithPrefix(@NotNull final ResourceEntry text) {
        final Text formattedValue = text.translate().asText();
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

    @Override
    public boolean hasPermission(@NotNull final String permission) {
        return true;
    }

    @Override
    public boolean hasAnyPermission(@NotNull final String @NotNull ... permissions) {
        return true;
    }

    @Override
    public boolean hasAllPermissions(@NotNull final String @NotNull ... permissions) {
        return true;
    }

    @Override
    public boolean hasPermission(@NotNull final Permission permission) {
        return true;
    }

    @Override
    public boolean hasAnyPermission(@NotNull final Permission @NotNull ... permissions) {
        return true;
    }

    @Override
    public boolean hasAllPermissions(@NotNull final Permission @NotNull ... permissions) {
        return true;
    }

    @Override
    public void permission(@NotNull final String permission, final Tristate state) {}

    @Override
    public void permissions(final Tristate state, @NotNull final String @NotNull ... permissions) {}

    @Override
    public void permission(@NotNull final Permission permission, final Tristate state) {}

    @Override
    public void permissions(final Tristate state, @NotNull final Permission @NotNull ... permissions) {}

    @Override
    public void execute(final String command) {
        try {
            this.context.get(CommandGateway.class).accept(this, command);
        }
        catch (final ParsingException e) {
            Except.handle(e);
        }
    }
}
