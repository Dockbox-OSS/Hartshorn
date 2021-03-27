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

package org.dockbox.selene.common.i18n;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import org.dockbox.selene.api.i18n.permissions.Permission;
import org.dockbox.selene.api.i18n.permissions.PermissionContext;
import org.dockbox.selene.api.objects.Exceptional;

public class SimplePermission implements Permission {

    private final String key;
    private final PermissionContext context;

    @AssistedInject
    public SimplePermission(
            @Assisted String key,
            @Assisted PermissionContext context
    ) {
        this.key = key;
        this.context = context;
    }

    @AssistedInject
    public SimplePermission(@Assisted String key) {
        this.key = key;
        this.context = null;
    }

    @Override
    public String get() {
        return this.key;
    }

    @Override
    public Exceptional<PermissionContext> getContext() {
        return Exceptional.ofNullable(this.context);
    }

    @Override
    public Permission withContext(PermissionContext context) {
        return new SimplePermission(this.key, context);
    }
}
