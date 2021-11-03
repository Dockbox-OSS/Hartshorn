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

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.i18n.annotations.Resource;
import org.dockbox.hartshorn.i18n.annotations.UseResources;
import org.dockbox.hartshorn.core.proxy.ProxyFunction;
import org.dockbox.hartshorn.core.context.MethodProxyContext;
import org.dockbox.hartshorn.core.services.ServiceAnnotatedMethodModifier;

public class I18NServiceModifier extends ServiceAnnotatedMethodModifier<Resource, UseResources> {

    @Override
    public <T, R> ProxyFunction<T, R> process(final ApplicationContext context, final MethodProxyContext<T> methodContext) {
        final String key = I18N.key(context, methodContext.type(), methodContext.method());
        final Resource annotation = methodContext.method().annotation(Resource.class).get();

        return (self, args, holder) -> {
            // Prevents NPE when formatting cached resources without arguments
            final Object[] objects = null == args ? new Object[0] : args;
            return (R) context.get(ResourceService.class).getOrCreate(key, annotation.value()).format(objects);
        };
    }

    @Override
    public <T> boolean preconditions(final ApplicationContext context, final MethodProxyContext<T> methodContext) {
        return methodContext.method().returnType().childOf(Message.class);
    }

    @Override
    public Class<Resource> annotation() {
        return Resource.class;
    }

    @Override
    public Class<UseResources> activator() {
        return UseResources.class;
    }
}
