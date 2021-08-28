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

package org.dockbox.hartshorn.test.util;

import org.dockbox.hartshorn.api.domain.tuple.Tristate;
import org.dockbox.hartshorn.di.annotations.service.Service;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.i18n.PermissionHolder;
import org.dockbox.hartshorn.i18n.permissions.Permission;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

@Service
public class JUnitPermissionRegistry {

    @Inject
    private ApplicationContext context;

    private final Map<UUID, Collection<Permission>> permissions = HartshornUtils.emptyConcurrentMap();

    private JUnitPermissionRegistry() {
    }

    public void permission(final PermissionHolder holder, final Permission permission) {
        this.permissions.putIfAbsent(holder.uniqueId(), HartshornUtils.emptyList());
        this.permissions.get(holder.uniqueId()).add(permission);
    }

    // Specific context
    public boolean hasPermission(final PermissionHolder holder, final Permission permission) {
        if (permission.context().absent()) return this.hasPermission(holder, permission.get());
        for (final Permission abstractPermission : this.permissions.getOrDefault(holder.uniqueId(), HartshornUtils.emptyList())) {
            if (abstractPermission.context().present() && abstractPermission.get().equals(permission.get())) {
                if (abstractPermission.context().get().equals(permission.context().get())) return true;
            }
        }
        return false;
    }

    // Global context
    public boolean hasPermission(final PermissionHolder holder, final String permission) {
        for (final Permission abstractPermission : this.permissions.getOrDefault(holder.uniqueId(), HartshornUtils.emptyList())) {
            if (abstractPermission.context().absent() && abstractPermission.get().equals(permission)) {
                return true;
            }
        }
        return false;
    }

    public void permission(final PermissionHolder holder, final String permission, final Tristate tristate) {
        if (tristate.booleanValue()) {
            this.permissions.get(holder.uniqueId()).add(Permission.of(this.context, permission));
        }
        else
            for (final Permission abstractPermission : this.permissions.getOrDefault(holder.uniqueId(), HartshornUtils.emptyList())) {
                if (abstractPermission.get().equals(permission) && abstractPermission.context().absent()) {
                    this.permissions.get(holder.uniqueId()).remove(abstractPermission);
                }
            }
    }

    public void permission(final PermissionHolder holder, final Permission permission, final Tristate tristate) {
        if (tristate.booleanValue()) {
            this.permissions.get(holder.uniqueId()).add(permission);
        }
        else
            for (final Permission abstractPermission : this.permissions.getOrDefault(holder.uniqueId(), HartshornUtils.emptyList())) {
                if (abstractPermission.equals(permission)) {
                    this.permissions.get(holder.uniqueId()).remove(abstractPermission);
                }
            }
    }
}
