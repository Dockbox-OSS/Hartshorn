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

import org.dockbox.hartshorn.core.context.ParameterLoaderContext;
import org.dockbox.hartshorn.core.context.element.ParameterContext;
import org.dockbox.hartshorn.core.services.parameter.RuleBasedParameterLoader;

public class UnproxyingParameterLoader extends RuleBasedParameterLoader<ParameterLoaderContext> {

    public UnproxyingParameterLoader() {
        this.add(new UnproxyParameterLoaderRule());
        this.add(new ObjectEqualsParameterLoaderRule());
    }

    @Override
    protected <T> T loadDefault(final ParameterContext<T> parameter, final int index, final ParameterLoaderContext context, final Object... args) {
        return (T) args[index];
    }
}
