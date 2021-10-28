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

package org.dockbox.hartshorn.i18n.permissions;

import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.i18n.common.Formattable;

@Deprecated(since = "4.2.3")
public interface Permission extends Formattable {

    @Deprecated(since = "4.2.3")
    static Permission of(final ApplicationContext context, final String node) {
        return context.get(Permission.class, node);
    }

    @Deprecated(since = "4.2.3")
    static Permission of(final ApplicationContext context, final String key, final PermissionContext permissionContext) {
        return context.get(Permission.class, key, permissionContext);
    }

    @Deprecated(since = "4.2.3")
    String get();

    @Deprecated(since = "4.2.3")
    Exceptional<PermissionContext> context();

    @Deprecated(since = "4.2.3")
    Permission withContext(PermissionContext context);
}
