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
