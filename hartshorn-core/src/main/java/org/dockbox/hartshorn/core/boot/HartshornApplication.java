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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.dockbox.hartshorn.core.Modifier;
import org.dockbox.hartshorn.core.annotations.activate.Activator;
import org.dockbox.hartshorn.core.boot.LogLevelModifier.LogLevel;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;

import java.lang.reflect.InvocationTargetException;

/**
 * Application starter for Hartshorn applications. This takes a single type annotated with {@link Activator}
 * which provides application metadata, and a set of {@link Modifier modifiers}.
 */
public final class HartshornApplication {

    private HartshornApplication() {}

    /**
     * Creates the bootstrapped server instance using the provided {@link Activator} metadata.
     *
     * @param activator
     *         The activator type, providing application metadata
     * @param modifiers
     *         The modifiers to use when bootstrapping
     */
    public static ApplicationContext create(final Class<?> activator, final String[] args, final Modifier... modifiers) {
        try {
            return load(TypeContext.of(activator), args, modifiers);
        }
        catch (final InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new ApplicationException("Could not bootstrap application " + activator.getSimpleName(), e).runtime();
        }
    }

    public static ApplicationContext load(
            final TypeContext<?> activator,
            final String[] args,
            final Modifier... modifiers
    ) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        for (final Modifier modifier : modifiers)
            if (modifier instanceof LogLevelModifier levelModifier) setLogLevel(levelModifier.level());

        return new HartshornApplicationFactory()
                .loadDefaults()
                .activator(activator)
                .arguments(args)
                .modifiers(modifiers)
                .create();
    }

    public static void setLogLevel(final LogLevel level) {
        final LoggerContext loggerContext = LoggerContext.getContext(false);
        final Configuration configuration = loggerContext.getConfiguration();
        final LoggerConfig loggerConfig = configuration.getLoggerConfig("org.dockbox.hartshorn");
        loggerConfig.setLevel(Level.getLevel(level.name()));
    }
}
