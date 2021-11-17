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

package org.dockbox.hartshorn.commands.arguments;

import org.dockbox.hartshorn.commands.context.CommandParameterContext;
import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.dockbox.hartshorn.core.context.element.ParameterContext;
import org.dockbox.hartshorn.core.services.parameter.ParameterLoader;
import org.dockbox.hartshorn.core.services.parameter.RuleBasedParameterLoader;

import javax.inject.Named;

@Binds(value = ParameterLoader.class, named = @Named("command_loader"))
public class CommandParameterLoader extends RuleBasedParameterLoader<CommandParameterLoaderContext> {

    public CommandParameterLoader() {
        this.add(new CommandContextParameterRule());
        this.add(new CommandSubjectParameterRule());
    }

    @Override
    protected <T> T loadDefault(final ParameterContext<T> parameter, final int index, final CommandParameterLoaderContext context, final Object... args) {
        final CommandParameterContext parameterContext = HartshornUtils.asList(context.executorContext().parameters().values()).get(index);
        final Object out = context.commandContext().get(parameterContext.parameter().name());
        return out == null ? super.loadDefault(parameter, index, context, args) : (T) out;
    }
}
