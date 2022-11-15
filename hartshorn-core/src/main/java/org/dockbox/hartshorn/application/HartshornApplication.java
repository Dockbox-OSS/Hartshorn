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

import java.util.function.Consumer;

/**
 * Application starter for Hartshorn applications. This takes a single type
 * which provides application metadata, and a set of command line arguments.
 *
 * @author Guus Lieben
 * @since 21.2
 */
public final class HartshornApplication {

    private HartshornApplication() {}

    /**
     * Creates a new application context for the given main class, arguments, and modifiers. This initializes the
     * required environment and starts the application.
     *
     * @param mainClass The main class
     * @param args The application arguments
     * @return The application context
     */
    public static ApplicationContext create(final Class<?> mainClass, final String... args) {
        return new StandardApplicationBuilder()
                .loadDefaults()
                .mainClass(mainClass)
                .arguments(args)
                .create();
    }

    public static ApplicationContext create(final Class<?> mainClass, final Consumer<ApplicationBuilder<?, ApplicationContext>> modifier) {
        final ApplicationBuilder<?, ApplicationContext> builder = new StandardApplicationBuilder()
                .loadDefaults()
                .mainClass(mainClass);
        modifier.accept(builder);
        return builder.create();
    }

    public static ApplicationContext create(final String... args) {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        final StackTraceElement element = stackTrace[2];
        try {
            final Class<?> mainClass = Class.forName(element.getClassName());
            return create(mainClass, args);
        }
        catch (final ClassNotFoundException e) {
            throw new IllegalStateException("Could not deduce main class", e);
        }
    }
}
