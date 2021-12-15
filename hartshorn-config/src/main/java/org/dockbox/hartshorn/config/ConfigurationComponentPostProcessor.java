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
import org.dockbox.hartshorn.core.context.element.ConstructorContext;
import org.dockbox.hartshorn.core.context.element.FieldContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.exceptions.NotPrimitiveException;
import org.dockbox.hartshorn.core.exceptions.TypeConversionException;
import org.dockbox.hartshorn.core.services.ComponentPostProcessor;

import java.util.Collection;

/**
 * Looks up and populates fields annotated with {@link Value}.
 */
@AutomaticActivation
public class ConfigurationComponentPostProcessor implements ComponentPostProcessor<UseConfigurations> {

    @Override
    public <T> boolean modifies(final ApplicationContext context, final TypeContext<T> type, @Nullable final T instance) {
        return !type.fields(Value.class).isEmpty();
    }

    @Override
    public <T> T process(final ApplicationContext context, final TypeContext<T> type, @Nullable final T instance) {
        TypeContext<?> instanceType = type;
        if (instance != null) instanceType = TypeContext.of(instance);

        for (final FieldContext<?> field : instanceType.fields(Value.class)) {
            try {
                final Value annotation = field.annotation(Value.class).get();

                final String key = annotation.value();
                final Exceptional<?> property;

                if (field.type().childOf(Collection.class)) {
                    final Exceptional<Collection<Object>> properties = context.properties(key);
                    // If a specific type was specified
                    if (!field.type().is(Collection.class)) {
                        final Exceptional<? extends ConstructorContext<?>> constructor = field.type().constructor(Collection.class);
                        if (constructor.absent()) throw new IllegalStateException("No compatible constructor found to convert collection to " + field.type().qualifiedName());
                        else {
                            final ConstructorContext<?> constructorContext = constructor.get();
                            final Collection<?> collection = (Collection<?>) constructorContext.createInstance(properties.get()).orNull();
                            property = Exceptional.of(collection);
                        }
                    } else {
                        property = properties;
                    }
                }
                else {
                    property = context.property(key);
                }

                if (property.absent()) {
                    context.log().debug("Property {} for field {} is empty, but field has a default value, using default value (note this may be null)", key, field.name());
                    continue;
                }

                Object value = property.get();
                context.log().debug("Populating value for configuration field '%s' in %s (type: %s), value is not logged.".formatted(field.name(), type.name(), field.type().name()));

                if ((!field.type().childOf(String.class)) && (value instanceof String stringValue)) {
                    value = TypeContext.toPrimitive(field.type(), stringValue);
                }
                field.set(instance, value);
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
