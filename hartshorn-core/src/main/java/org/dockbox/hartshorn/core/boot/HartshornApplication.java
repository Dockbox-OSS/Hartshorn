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

import org.dockbox.hartshorn.core.Modifiers;
import org.dockbox.hartshorn.core.annotations.activate.Activator;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.lang.management.ManagementFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * Application starter for Hartshorn applications. This takes a single type annotated with {@link Activator}
 * which provides application metadata, and a set of {@link Modifiers modifiers}.
 *
 * @author Guus Lieben
 * @since 21.2
 */
public final class HartshornApplication {

    private HartshornApplication() {}

    /**
     * Creates the bootstrapped server instance using the provided {@link Activator} metadata.
     *
     * @param activator The activator type, providing application metadata
     * @param modifiers The modifiers to use when bootstrapping
     * @see Modifiers
     */
    public static ApplicationContext create(final Class<?> activator, final String[] args, final Modifiers... modifiers) {
        for (final Modifiers modifier : modifiers)
            if (modifier == Modifiers.DEBUG) setDebugActive();

        MDC.put("process_id", ManagementFactory.getRuntimeMXBean().getName());

        return new HartshornApplicationFactory()
                .loadDefaults()
                .activator(TypeContext.of(activator))
                .arguments(args)
                .modifiers(modifiers)
                .create();
    }

    private static void setDebugActive() {
        final ILoggerFactory factory = LoggerFactory.getILoggerFactory();

        if (factory instanceof LoggerContext loggerContext) {
            for (final Logger logger : loggerContext.getLoggerList()) {
                logger.setLevel(Level.DEBUG);
            }
        }
        else {
            final Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
            rootLogger.setLevel(Level.DEBUG);
        }
    }
}
