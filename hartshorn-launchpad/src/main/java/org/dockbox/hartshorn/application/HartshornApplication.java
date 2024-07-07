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

import org.dockbox.hartshorn.launchpad.ApplicationContext;
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
     * @param arguments The application arguments
     * @return The application context
     */
    public static ApplicationContext create(Class<?> mainClass, String... arguments) {
        return createApplication(mainClass, arguments).initialize();
    }

    /**
     * Creates a new application bootstrap for the given main class, arguments, and modifiers. This initializes the
     * required environment and starts the application.
     *
     * @param mainClass The main class
     * @param arguments The application arguments
     * @return The application context
     */
    public static ApplicationBootstrap createApplication(Class<?> mainClass, String... arguments) {
        return customizer -> HartshornApplicationConfigurer.createInitializer(builder -> {
            builder.mainClass(mainClass);
            builder.arguments(arguments);
        }, customizer).initialize();
    }

    /**
     * Creates a new application context for the given arguments, and modifiers. This initializes the
     * required environment and starts the application. The main class will be inferred from the stack trace.
     * This is useful for when you want to start an application from a main method, but don't want to
     * hard-code the main class.
     *
     * @param arguments The application arguments
     * @return The application context
     */
    public static ApplicationContext create(String... arguments) {
        return createApplication(arguments).initialize();
    }

    /**
     * Creates a new application bootstrap for the given arguments, and modifiers. This initializes the
     * required environment and starts the application. The main class will be inferred from the stack trace.
     * This is useful for when you want to start an application from a main method, but don't want to
     * hard-code the main class.
     *
     * @param arguments The application arguments
     * @return The application context
     */
    public static ApplicationBootstrap createApplication(String... arguments) {
        return customizer -> HartshornApplicationConfigurer.createInitializer(builder -> {
            builder.inferMainClass();
            builder.arguments(arguments);
        }, customizer).initialize();
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
        Customizer<StandardApplicationBuilder.Configurer> defaultCustomizer = builder -> builder.mainClass(mainClass);
        return create(defaultCustomizer.compose(customizer));
    }

    /**
     * Creates a new application context for the given main class, and allows for customizing the application
     * builder. This allows complete control over the application context creation process. This initializes the
     * required environment and starts the application. The main class will be inferred from the stack trace.
     *
     * @param customizer The application builder customizer
     * @return The application context
     */
    public static ApplicationContext create(Customizer<StandardApplicationBuilder.Configurer> customizer) {
        Customizer<StandardApplicationBuilder.Configurer> defaultCustomizer = StandardApplicationBuilder.Configurer::inferMainClass;
        return HartshornApplicationConfigurer.createInitializer(
            defaultCustomizer.compose(customizer),
            Customizer.useDefaults()
        ).initialize();
    }

    /**
     * Deferred application bootstrap. This allows for customizing the application through a high-level {@link
     * HartshornApplicationConfigurer}, rather than the low-level {@link StandardApplicationBuilder.Configurer}.
     *
     * @since 0.6.0
     *
     * @author Guus Lieben
     */
    public interface ApplicationBootstrap {

        /**
         * Initializes the application context, using the default configuration.
         *
         * @return The application context
         */
        default ApplicationContext initialize() {
            return this.initialize(Customizer.useDefaults());
        }

        /**
         * Initializes the application context, applying the provided configuration customizer before starting the
         * application.
         *
         * @param customizer The customizer to apply
         * @return The application context
         */
        ApplicationContext initialize(Customizer<HartshornApplicationConfigurer> customizer);
    }
}
