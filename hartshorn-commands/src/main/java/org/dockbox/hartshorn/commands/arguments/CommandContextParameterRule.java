/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.commands.arguments;

import org.dockbox.hartshorn.core.context.element.ParameterContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.services.parameter.ParameterLoaderRule;

public class CommandContextParameterRule implements ParameterLoaderRule<CommandParameterLoaderContext> {

    @Override
    public boolean accepts(final ParameterContext<?> parameter, final CommandParameterLoaderContext context, final Object... args) {
        return TypeContext.of(context.commandContext()).childOf(parameter.type());
    }

    @Override
    public <T> Exceptional<T> load(final ParameterContext<T> parameter, final CommandParameterLoaderContext context, final Object... args) {
        return Exceptional.of((T) context.commandContext());
    }
}
