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

import org.dockbox.selene.api.i18n.common.Formattable;
import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.server.Selene;
import org.dockbox.selene.api.server.SeleneFactory;

public interface Permission extends Formattable {

    static Permission of(String node) {
        return Selene.provide(SeleneFactory.class).permission(node);
    }

    static Permission of(String key, PermissionContext context) {
        return Selene.provide(SeleneFactory.class).permission(key, context);
    }

    String get();
    Exceptional<PermissionContext> getContext();
    Permission withContext(PermissionContext context);
}
