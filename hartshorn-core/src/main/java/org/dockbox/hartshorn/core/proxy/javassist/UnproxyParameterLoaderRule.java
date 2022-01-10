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

package org.dockbox.hartshorn.core.proxy.javassist;

import org.dockbox.hartshorn.core.annotations.Unproxy;
import org.dockbox.hartshorn.core.context.ParameterLoaderContext;
import org.dockbox.hartshorn.core.context.element.ParameterContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.proxy.ProxyHandler;
import org.dockbox.hartshorn.core.services.parameter.ParameterLoaderRule;

public class UnproxyParameterLoaderRule implements ParameterLoaderRule<ParameterLoaderContext> {
    @Override
    public boolean accepts(final ParameterContext<?> parameter, final int index, final ParameterLoaderContext context, final Object... args) {
        return parameter.annotation(Unproxy.class).present() || parameter.declaredBy().annotation(Unproxy.class).present();
    }

    @Override
    public <T> Exceptional<T> load(final ParameterContext<T> parameter, final int index, final ParameterLoaderContext context, final Object... args) {
        final Object argument = args[index];
        final Exceptional<ProxyHandler<Object>> handler = context.applicationContext().environment().manager().handler(argument);
        return handler.flatMap(ProxyHandler::instance).orElse(() -> {
            final Unproxy unproxy = parameter.annotation(Unproxy.class).orElse(() -> parameter.declaredBy().annotation(Unproxy.class).orNull()).get();
            if (unproxy.fallbackToProxy()) return argument;
            else return null;
        }).map(a -> (T) a);
    }
}
