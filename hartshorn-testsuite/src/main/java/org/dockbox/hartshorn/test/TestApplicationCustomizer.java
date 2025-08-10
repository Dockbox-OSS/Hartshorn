/*
 * Copyright 2019-2025 the original author or authors.
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

package org.dockbox.hartshorn.test;

import org.dockbox.hartshorn.launchpad.SimpleApplicationContext;
import org.dockbox.hartshorn.launchpad.environment.ConfigurableApplicationEnvironment;
import org.dockbox.hartshorn.launchpad.launch.StandardApplicationBuilder;
import org.dockbox.hartshorn.launchpad.launch.StandardApplicationContextFactory;

/**
 * An interface for customizing the application context in a test environment. This interface is practically a
 * composed {@link org.dockbox.hartshorn.util.configure.Customizer} for the various predictable components of the
 * application context.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface TestApplicationCustomizer {

    /**
     * Customizes the application builder. The application builder allows for early configuration of the
     * application context, by consuming the main class and arguments.
     *
     * @param configurer the configurer to customize the application builder
     */
    default void customizeBuilder(StandardApplicationBuilder.Configurer configurer) {
        // Default implementation does nothing
    }

    /**
     * Customizes the application environment. The application environment allows for configuration of the
     * application's environment, such as properties and profiles, as well as commonly used low-level
     * components.
     *
     * @param configurer the configurer to customize the application environment
     */
    default void customizeEnvironment(ConfigurableApplicationEnvironment.Configurer configurer) {
        // Default implementation does nothing
    }

    /**
     * Customizes the application factory. The application factory is responsible for creating the required
     * base application components to discover and process components.
     *
     * @param configurer the configurer to customize the application factory
     */
    default void customizeFactory(StandardApplicationContextFactory.Configurer configurer) {
        // Default implementation does nothing
    }

    /**
     * Customizes the application context. The application context is the main entry point for the application,
     * and allows for configuration of the application context itself, such as component bindings and the default
     * component provider.
     *
     * @param configurer the configurer to customize the application context
     */
    default void customizeApplication(SimpleApplicationContext.Configurer configurer) {
        // Default implementation does nothing
    }
}
