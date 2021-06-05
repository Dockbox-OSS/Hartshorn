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

package org.dockbox.hartshorn.commands;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.commands.annotations.UseCommands;
import org.dockbox.hartshorn.commands.context.ArgumentConverter;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.properties.InjectableType;
import org.dockbox.hartshorn.di.services.ServiceProcessor;
import org.dockbox.hartshorn.util.Reflect;

import java.lang.reflect.Field;
import java.util.List;

public class ArgumentServiceProcessor implements ServiceProcessor<UseCommands> {

    @Override
    public boolean preconditions(Class<?> type) {
        List<Field> fields = Reflect.fieldsWithSuper(type, ArgumentConverter.class);
        return !fields.isEmpty();
    }

    @Override
    public <T> void process(ApplicationContext context, Class<T> type) {
        final T raw = context.raw(type);
        if (raw instanceof InjectableType) {
            Exceptional.of(() -> {
                // If a ApplicationException is thrown this fails silently
                ((InjectableType) raw).stateEnabling();
                return null; // Captured in Exceptional
            });
        }
    }

    @Override
    public Class<UseCommands> activator() {
        return UseCommands.class;
    }
}