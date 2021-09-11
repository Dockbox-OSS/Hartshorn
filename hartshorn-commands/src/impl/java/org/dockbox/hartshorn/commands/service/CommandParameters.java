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

package org.dockbox.hartshorn.commands.service;

import org.dockbox.hartshorn.boot.annotations.PostBootstrap;
import org.dockbox.hartshorn.boot.annotations.UseBootstrap;
import org.dockbox.hartshorn.commands.annotations.Parameter;
import org.dockbox.hartshorn.commands.arguments.CustomParameterPattern;
import org.dockbox.hartshorn.commands.arguments.DynamicPatternConverter;
import org.dockbox.hartshorn.commands.context.ArgumentConverterContext;
import org.dockbox.hartshorn.commands.definition.ArgumentConverter;
import org.dockbox.hartshorn.di.annotations.service.Service;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.element.TypeContext;

import java.util.Collection;

/**
 * Scans for any type annotated with {@link Parameter} and registers a {@link DynamicPatternConverter}
 * for each type found. Requires the use of a {@link org.dockbox.hartshorn.di.InjectableBootstrap} and
 * presence of {@link UseBootstrap}.
 */
@Service(activators = UseBootstrap.class)
public class CommandParameters {

    @PostBootstrap
    public void preload(final ApplicationContext context) {
        final Collection<TypeContext<?>> customParameters = context.environment().types(Parameter.class);
        for (final TypeContext<?> customParameter : customParameters) {
            final Parameter meta = customParameter.annotation(Parameter.class).get();
            final CustomParameterPattern pattern = context.get(meta.pattern());
            final String key = meta.value();
            final ArgumentConverter<?> converter = new DynamicPatternConverter<>(customParameter, pattern, key);
            context.first(ArgumentConverterContext.class).present(converterContext -> converterContext.register(converter));
        }
    }

}
