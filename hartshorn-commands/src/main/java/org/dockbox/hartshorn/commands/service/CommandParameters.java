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

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.annotations.PostBootstrap;
import org.dockbox.hartshorn.api.annotations.UseBootstrap;
import org.dockbox.hartshorn.commands.arguments.CustomParameterPattern;
import org.dockbox.hartshorn.commands.annotations.Parameter;
import org.dockbox.hartshorn.commands.arguments.DynamicPatternConverter;
import org.dockbox.hartshorn.di.annotations.Service;
import org.dockbox.hartshorn.util.Reflect;

import java.util.Collection;

@Service(activators = UseBootstrap.class)
public class CommandParameters {

    @PostBootstrap
    public void preload() {
        Collection<Class<?>> customParameters = Reflect.types(Parameter.class);
        for (Class<?> customParameter : customParameters) {
            Parameter meta = customParameter.getAnnotation(Parameter.class);
            CustomParameterPattern pattern = Hartshorn.context().get(meta.pattern());
            String key = meta.value();
            // Automatically registers to the ArgumentConverterRegistry
            new DynamicPatternConverter<>(customParameter, pattern, key);
        }
    }

}
