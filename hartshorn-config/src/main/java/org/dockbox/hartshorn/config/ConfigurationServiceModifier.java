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

import org.dockbox.hartshorn.api.exceptions.Except;
import org.dockbox.hartshorn.boot.Hartshorn;
import org.dockbox.hartshorn.config.annotations.UseConfigurations;
import org.dockbox.hartshorn.config.annotations.Value;
import org.dockbox.hartshorn.di.NotPrimitiveException;
import org.dockbox.hartshorn.di.TypeConversionException;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.element.FieldContext;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.di.inject.InjectionModifier;
import org.dockbox.hartshorn.di.properties.Attribute;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.dockbox.hartshorn.util.exceptions.FieldAccessException;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Looks up and populates fields annotated with {@link Value}.
 */
public class ConfigurationServiceModifier implements InjectionModifier<UseConfigurations> {

    @Override
    public <T> boolean preconditions(final ApplicationContext context, final TypeContext<T> type, @Nullable final T instance, final Attribute<?>... properties) {
        TypeContext<?> instanceType = type;
        if (instance != null) instanceType = TypeContext.of(instance);
        final boolean decorated = this.isAnnotated(context, instanceType);
        return decorated && !instanceType.fields(Value.class).isEmpty();
    }

    private boolean isAnnotated(final ApplicationContext context, final TypeContext<?> type) {
        return context.locator().container(type).present();
    }

    @Override
    public <T> T process(final ApplicationContext context, final TypeContext<T> type, @Nullable final T instance, final Attribute<?>... properties) {
        TypeContext<?> instanceType = type;
        if (instance != null) instanceType = TypeContext.of(instance);

        for (final FieldContext<?> field : instanceType.fields(Value.class)) {
            try {
                final Value value = field.annotation(Value.class).get();
                Object fieldValue = context.property(value.value()).or(value.or());

                context.log().debug("Populating value for configuration field " + field.name() + " in " + type.name() + " (type: " + field.type().name() + ", value: " + fieldValue);

                if ((!field.type().childOf(String.class)) && (fieldValue instanceof String stringValue)) {
                    try {
                        fieldValue = TypeContext.toPrimitive(field.type(), stringValue);
                    } catch (final NotPrimitiveException e) {
                        if (field.type().childOf(Collection.class)) {
                            if ("".equals(stringValue)) {
                                if (field.type().childOf(List.class)) fieldValue = HartshornUtils.emptyList();
                                if (field.type().childOf(Set.class)) fieldValue = HartshornUtils.emptySet();
                            } else {
                                Hartshorn.log().warn("Cannot convert string '" + stringValue + "' to collection type");
                            }
                        }
                        else throw e;
                    }
                }
                field.set(instance, fieldValue);
            }
            catch (final FieldAccessException | TypeConversionException | NotPrimitiveException e) {
                Hartshorn.log().warn("Could not prepare value field " + field.name() + " in " + instanceType.name());
                Except.handle(e);
            }
        }

        return instance;
    }

    @Override
    public Class<UseConfigurations> activator() {
        return UseConfigurations.class;
    }
}
