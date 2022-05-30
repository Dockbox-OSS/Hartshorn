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
import org.dockbox.hartshorn.util.reflect.TypeContext;

/**
 * Application starter for Hartshorn applications. This takes a single type annotated with {@link Activator}
 * which provides application metadata, and a set of command line arguments.
 *
 * @author Guus Lieben
 * @since 21.2
 */
public final class HartshornApplication {

    private HartshornApplication() {}

    /**
     * Creates a new application context for the given application type, arguments, and modifiers. This initializes the
     * required environment and starts the application.
     *
     * @param activator The application type annotated with {@link Activator}
     * @param args The application arguments
     * @return The application context
     */
    public static ApplicationContext create(final Class<?> activator, final String... args) {
        return new StandardApplicationFactory()
                .loadDefaults()
                .activator(TypeContext.of(activator))
                .arguments(args)
                .create();
    }

    /**
     * Creates a new application context using the provided arguments and modifiers. This is a convenience method
     * which deduces the activator type from the current thread's stacktrace.
     *
     * @param args The arguments to use when bootstrapping
     * @return The application context
     */
    public static ApplicationContext create(final String... args) {
        return new StandardApplicationFactory()
                .loadDefaults()
                .deduceActivator()
                .arguments(args)
                .create();
    }
}
