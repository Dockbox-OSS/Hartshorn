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

package org.dockbox.hartshorn.core.boot;

import org.dockbox.hartshorn.core.InjectConfiguration;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.core.exceptions.TypeConversionException;

import java.util.Set;

/**
 * This class is responsible for configuring the application manager. This default implementation of the
 * {@link ApplicationConfigurator} redirects binding to the active {@link ApplicationContext} and uses the
 * {@code hartshorn.exceptions.stacktraces} property to enable or disable stacktraces.
 *
 * @author Guus Lieben
 * @since 4.2.4
 */
public class HartshornApplicationConfigurator implements ApplicationConfigurator {

    @Override
    public void configure(final ApplicationManager manager) {
        manager.stacktraces(this.stacktraces(manager));
    }

    @Override
    public void apply(final ApplicationManager manager, final Set<InjectConfiguration> configurations) {
        for (final InjectConfiguration config : configurations)
            manager.applicationContext().bind(config);
    }

    @Override
    public void bind(final ApplicationManager manager, final String prefix) {
        manager.applicationContext().bind(prefix);
    }

    /**
     * Returns whether stacktraces should be enabled. Uses the {@code hartshorn.exceptions.stacktraces} property
     * to determine this.
     *
     * @param manager The application manager.
     * @return Whether stacktraces should be enabled.
     */
    protected boolean stacktraces(final ApplicationManager manager) {
        return this.primitiveProperty(manager, "hartshorn.exceptions.stacktraces", boolean.class, true);
    }

    /**
     * Returns the value of the specified property as the specified primitive type, or the default value if the property is
     * not set.
     *
     * @param manager The application manager.
     * @param property The property to get.
     * @param type The type of the property.
     * @param defaultValue The default value of the property.
     * @param <T> The type of the property.
     * @return The value of the specified property as the specified primitive type, or the default value.
     */
    protected <T> T primitiveProperty(final ApplicationManager manager, final String property, final Class<T> type, final T defaultValue) {
        return manager.applicationContext()
                .property(property)
                .map(value -> {
                    try {
                        if (value instanceof String str)
                            return TypeContext.toPrimitive(TypeContext.of(type), str);
                        else return (T) value;
                    } catch (final TypeConversionException e) {
                        throw new ApplicationException(e);
                    }
                })
                .or(defaultValue);
    }
}
