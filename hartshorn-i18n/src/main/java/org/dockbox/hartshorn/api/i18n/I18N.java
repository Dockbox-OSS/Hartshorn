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

package org.dockbox.hartshorn.api.i18n;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.MetaProvider;
import org.dockbox.hartshorn.api.domain.TypedOwner;
import org.dockbox.hartshorn.api.i18n.annotations.Resource;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.lang.reflect.Method;

public final class I18N {

    private I18N() {
    }

    public static String key(Class<?> type, Method method) {
        String prefix = "";

        final MetaProvider provider = Hartshorn.context().meta();
        if (provider.isComponent(type)) {
            TypedOwner lookup = provider.lookup(type);
            if (lookup != null) prefix = lookup.id() + '.';
        }

        return extract(method, prefix);
    }

    private static String extract(Method method, String prefix) {
        if (method.isAnnotationPresent(Resource.class)) {
            String key = method.getAnnotation(Resource.class).key();
            if (!"".equals(key)) return key;
        }
        String keyJoined = method.getName();
        if (keyJoined.startsWith("get")) keyJoined = keyJoined.substring(3);
        String[] r = HartshornUtils.splitCapitals(keyJoined);
        return prefix + String.join(".", r).toLowerCase();
    }

}
