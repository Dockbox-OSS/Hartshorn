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

package org.dockbox.selene.api.i18n.permissions;

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.di.annotations.AutoWired;
import org.dockbox.selene.di.annotations.Binds;

@Binds(Permission.class)
public class SimplePermission implements Permission {

    private final String key;
    private final PermissionContext context;

    @AutoWired
    public SimplePermission(String key, PermissionContext context) {
        this.key = key;
        this.context = context;
    }

    @AutoWired
    public SimplePermission(String key) {
        this.key = key;
        this.context = null;
    }

    @Override
    public String get() {
        return this.key;
    }

    @Override
    public Exceptional<PermissionContext> getContext() {
        return Exceptional.of(this.context);
    }

    @Override
    public Permission withContext(PermissionContext context) {
        return new SimplePermission(this.key, context);
    }
}
