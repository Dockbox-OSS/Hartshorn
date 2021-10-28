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

package org.dockbox.hartshorn.i18n;

import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.domain.TypedOwner;
import org.dockbox.hartshorn.core.MetaProvider;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.i18n.annotations.Resource;
import org.dockbox.hartshorn.core.HartshornUtils;

public final class I18N {

    private I18N() {
    }

    public static String key(final ApplicationContext context, final TypeContext<?> type, final MethodContext<?, ?> method) {
        String prefix = "";

        final MetaProvider provider = context.meta();
        if (provider.isComponent(type)) {
            final TypedOwner lookup = provider.lookup(type);
            if (lookup != null) prefix = lookup.id() + '.';
        }

        final String extracted = extract(method, prefix);
        context.log().debug("Determined I18N key for %s: %s".formatted(method.qualifiedName(), extracted));
        return extracted;
    }

    private static String extract(final MethodContext<?, ?> method, final String prefix) {
        final Exceptional<Resource> resource = method.annotation(Resource.class);
        if (resource.present()) {
            final String key = resource.get().key();
            if (!"".equals(key)) return key;
        }
        String keyJoined = method.name();
        if (keyJoined.startsWith("get")) keyJoined = keyJoined.substring(3);
        final String[] r = HartshornUtils.splitCapitals(keyJoined);
        return prefix + String.join(".", r).toLowerCase();
    }

}
