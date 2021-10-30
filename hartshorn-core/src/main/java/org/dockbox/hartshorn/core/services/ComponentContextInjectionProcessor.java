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

package org.dockbox.hartshorn.core.services;

import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.core.annotations.inject.Context;
import org.dockbox.hartshorn.core.annotations.service.Service;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.ExecutableElementContext;
import org.dockbox.hartshorn.core.context.element.FieldContext;
import org.dockbox.hartshorn.core.context.element.ParameterContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.context.element.TypedElementContext;
import org.dockbox.hartshorn.core.HartshornUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ComponentContextInjectionProcessor extends ComponentValidator<Service>{

    @Override
    public Class<Service> activator() {
        return Service.class;
    }

    @Override
    public <T> void process(final ApplicationContext context, final TypeContext<T> type) {
        for (final FieldContext<?> field : type.fields(Context.class))
            this.validate(field, type);

        final List<ExecutableElementContext<?>> constructors = type.injectConstructors().stream().map(c -> (ExecutableElementContext<?>) c).collect(Collectors.toList());
        final List<ExecutableElementContext<?>> methods = type.methods().stream().map(m -> (ExecutableElementContext<?>) m).collect(Collectors.toList());
        final Collection<ExecutableElementContext<?>> executables = HartshornUtils.merge(constructors, methods);

        for (final ExecutableElementContext<?> executable : executables)
            for (final ParameterContext<?> parameter : executable.parameters(Context.class))
                this.validate(parameter, type);
    }

    private void validate(final TypedElementContext<?> context, final TypeContext<?> parent) {
        if (!context.type().childOf(org.dockbox.hartshorn.core.context.Context.class))
            throw new ApplicationException("%s is annotated with %s but does not extend %s".formatted(
                    context.qualifiedName(),
                    Context.class.getSimpleName(),
                    org.dockbox.hartshorn.core.context.Context.class.getSimpleName())
            ).runtime();
    }
}
