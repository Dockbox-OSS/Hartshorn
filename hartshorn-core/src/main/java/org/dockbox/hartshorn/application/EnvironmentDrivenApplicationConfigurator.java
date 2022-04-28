/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.application;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.environment.ApplicationManager;
import org.dockbox.hartshorn.inject.binding.InjectConfiguration;
import org.dockbox.hartshorn.util.reflect.TypeContext;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.TypeConversionException;

import java.util.Set;

/**
 * This class is responsible for configuring the application manager. This default implementation of the
 * {@link ApplicationConfigurator} redirects binding to the active {@link ApplicationContext} and uses the
 * {@code hartshorn.exceptions.stacktraces} property to enable or disable stacktraces.
 *
 * @author Guus Lieben
 * @since 21.9
 */
public class EnvironmentDrivenApplicationConfigurator implements ApplicationConfigurator {

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