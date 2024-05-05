/*
 * Copyright 2019-2024 the original author or authors.
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

import org.dockbox.hartshorn.application.StandardApplicationBuilder.Configurer;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.Customizer;

/**
 * Application starter for Hartshorn applications. This takes a single type
 * which provides application metadata, and a set of command line arguments.
 *
 * @since 0.4.1
 *
 * @author Guus Lieben
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
    public static ApplicationContext create(Class<?> mainClass, String... args) {
        return StandardApplicationBuilder.create(builder -> {
            builder.mainClass(mainClass);
            builder.arguments(args);
        }).create();
    }

    /**
     * Creates a new application context for the given arguments, and modifiers. This initializes the
     * required environment and starts the application. The main class will be inferred from the stack trace.
     * This is useful for when you want to start an application from a main method, but don't want to
     * hard-code the main class.
     *
     * @param args The application arguments
     * @return The application context
     */
    public static ApplicationContext create(String... args) {
        return StandardApplicationBuilder.create(builder -> {
            builder.inferMainClass();
            builder.arguments(args);
        }).create();
    }

    /**
     * Creates a new application context for the given main class, and allows for customizing the application
     * builder. This allows complete control over the application context creation process. This initializes the
     * required environment and starts the application.
     *
     * @param mainClass The main class
     * @param customizer The application builder customizer
     * @return The application context
     */
    public static ApplicationContext create(Class<?> mainClass, Customizer<StandardApplicationBuilder.Configurer> customizer) {
        Customizer<Configurer> defaultCustomizer = builder -> builder.mainClass(mainClass);
        Customizer<Configurer> composedCustomizer = defaultCustomizer.compose(customizer);
        return StandardApplicationBuilder.create(composedCustomizer).create();
    }
}
