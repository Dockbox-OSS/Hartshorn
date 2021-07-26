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

import org.dockbox.hartshorn.api.exceptions.ApplicationException;
import org.dockbox.hartshorn.commands.annotations.UseCommands;
import org.dockbox.hartshorn.commands.context.ArgumentConverterContext;
import org.dockbox.hartshorn.commands.definition.ArgumentConverter;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.services.ServiceProcessor;
import org.dockbox.hartshorn.util.Reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

public class ArgumentServiceProcessor implements ServiceProcessor<UseCommands> {

    @Override
    public boolean preconditions(Class<?> type) {
        List<Field> fields = Reflect.fieldsLike(type, ArgumentConverter.class);
        return !fields.isEmpty();
    }

    @Override
    public <T> void process(ApplicationContext context, Class<T> type) {
        final List<Field> fields = Reflect.fieldsLike(type, ArgumentConverter.class);
        context.first(ArgumentConverterContext.class).map(converterContext -> {
            for (Field field : fields) {
                field.setAccessible(true);
                if (Modifier.isStatic(field.getModifiers())) {
                    try {
                        final ArgumentConverter<?> converter = (ArgumentConverter<?>) field.get(null);
                        converterContext.register(converter);
                    }
                    catch (IllegalAccessException e) {
                        throw new ApplicationException(e);
                    }
                } else {
                    throw new ApplicationException(field.getName() + " should be static");
                }
            }
            return null;
        }).rethrow();
    }

    @Override
    public Class<UseCommands> activator() {
        return UseCommands.class;
    }
}
