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

package org.dockbox.hartshorn.config;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.config.annotations.UseConfigurations;
import org.dockbox.hartshorn.config.annotations.Value;
import org.dockbox.hartshorn.core.annotations.service.AutomaticActivation;
import org.dockbox.hartshorn.core.boot.Hartshorn;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.FieldContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.exceptions.NotPrimitiveException;
import org.dockbox.hartshorn.core.exceptions.TypeConversionException;
import org.dockbox.hartshorn.core.services.ComponentPostProcessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Looks up and populates fields annotated with {@link Value}.
 */
@AutomaticActivation
public class ConfigurationServicePostProcessor implements ComponentPostProcessor<UseConfigurations> {

    @Override
    public <T> boolean modifies(ApplicationContext context, TypeContext<T> type, @Nullable T instance) {
        return !type.fields(Value.class).isEmpty();
    }

    @Override
    public <T> T process(final ApplicationContext context, final TypeContext<T> type, @Nullable final T instance) {
        TypeContext<?> instanceType = type;
        if (instance != null) instanceType = TypeContext.of(instance);

        for (final FieldContext<?> field : instanceType.fields(Value.class)) {
            try {
                final Value value = field.annotation(Value.class).get();
                Object fieldValue = context.property(value.value()).or(value.or());

                context.log().debug("Populating value for configuration field '%s' in %s (type: %s, value: %s)".formatted(field.name(), type.name(), field.type().name(), fieldValue));

                if ((!field.type().childOf(String.class)) && (fieldValue instanceof String stringValue)) {
                    try {
                        fieldValue = TypeContext.toPrimitive(field.type(), stringValue);
                    } catch (final NotPrimitiveException e) {
                        if (field.type().childOf(Collection.class)) {
                            if ("".equals(stringValue)) {
                                if (field.type().childOf(List.class)) fieldValue = new ArrayList<>();
                                if (field.type().childOf(Set.class)) fieldValue = new HashSet<>();
                            } else {
                                Hartshorn.log().warn("Cannot convert string '" + stringValue + "' to collection type");
                            }
                        }
                        else throw e;
                    }
                }
                field.set(instance, fieldValue);
            }
            catch (final TypeConversionException | NotPrimitiveException e) {
                Hartshorn.log().warn("Could not prepare value field " + field.name() + " in " + instanceType.name());
                context.handle(e);
            }
        }

        return instance;
    }

    @Override
    public Class<UseConfigurations> activator() {
        return UseConfigurations.class;
    }
}
