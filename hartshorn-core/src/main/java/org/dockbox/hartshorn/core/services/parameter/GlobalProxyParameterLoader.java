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

package org.dockbox.hartshorn.core.services.parameter;

import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.dockbox.hartshorn.core.annotations.proxy.Instance;
import org.dockbox.hartshorn.core.context.ParameterLoaderContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;

import java.util.Arrays;
import java.util.List;

import javax.inject.Named;

@Binds(value = ParameterLoader.class, named = @Named("global_proxy"))
public class GlobalProxyParameterLoader extends ParameterLoader<ParameterLoaderContext> {

    @Override
    public List<Object> loadArguments(final ParameterLoaderContext context, final Object... args) {
        final MethodContext<?, ?> method = context.method();
        final List<Object> arguments = HartshornUtils.emptyList();
        if (method.parameterCount() >= 1 && method.parameters().get(0).annotation(Instance.class).present()) {
            arguments.add(context.instance());
        }
        arguments.addAll(Arrays.asList(args));
        return arguments;
    }
}
