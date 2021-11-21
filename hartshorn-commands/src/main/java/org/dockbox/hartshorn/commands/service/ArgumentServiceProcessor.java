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

import org.dockbox.hartshorn.core.annotations.service.AutomaticActivation;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.commands.annotations.UseCommands;
import org.dockbox.hartshorn.commands.context.ArgumentConverterContext;
import org.dockbox.hartshorn.commands.definition.ArgumentConverter;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.FieldContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.services.ServiceOrder;
import org.dockbox.hartshorn.core.services.ServiceProcessor;

import java.util.List;

/**
 * Processes any service with static {@link ArgumentConverter} fields, and registers them to
 * the {@link ArgumentConverterContext} contained in the {@link ApplicationContext}. Requires
 * the presence of {@link UseCommands}.
 */
@AutomaticActivation
public class ArgumentServiceProcessor implements ServiceProcessor<UseCommands> {

    @Override
    public boolean preconditions(final ApplicationContext context, final TypeContext<?> type) {
        return !type.fieldsOf(ArgumentConverter.class).isEmpty();
    }

    @Override
    public <T> void process(final ApplicationContext context, final TypeContext<T> type) {
        final List<FieldContext<ArgumentConverter>> fields = type.fieldsOf(ArgumentConverter.class);
        context.log().debug("Found %d argument converters in %s".formatted(fields.size(), type.qualifiedName()));
        context.first(ArgumentConverterContext.class).map(converterContext -> {
            for (final FieldContext<ArgumentConverter> field : fields) {
                if (field.isStatic()) {
                    final Exceptional<ArgumentConverter> converter = field.getStatic();
                    converter.present(converterContext::register);
                }
                else {
                    throw new ApplicationException(field.name() + " should be static");
                }
            }
            return null;
        }).rethrowUnchecked();
    }

    @Override
    public Class<UseCommands> activator() {
        return UseCommands.class;
    }

    @Override
    public ServiceOrder order() {
        return ServiceOrder.FIRST;
    }
}
