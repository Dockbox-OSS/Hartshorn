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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.inject.binding.Binder;

/**
 * A functional interface used to configure the default bindings of the {@link ApplicationEnvironment}. This interface
 * should be used to configure the default bindings of the application. This interface is typically provided by the
 * {@link Binder} attached to the {@link ApplicationEnvironment}, which is often the {@link ApplicationContext}.
 *
 * <p>Implementations of this interface are expected to be stateless, and thread-safe.
 *
 * <p>Additional bindings can be added by providing a {@link DefaultBindingConfigurer}, which is processed after the
 * default bindings. This is useful when the default bindings are not sufficient, or when the default bindings are not
 * desired.
 *
 * @see Binder
 * @see ApplicationEnvironment
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
@FunctionalInterface
public interface EnvironmentBinderConfiguration {

    /**
     * Configures the default bindings of the application.
     *
     * @param environment The environment to configure.
     * @param configurer The configurer to use for additional bindings.
     * @param binder The binder to use for additional bindings.
     */
    void configureBindings(ApplicationEnvironment environment, DefaultBindingConfigurer configurer, Binder binder);
}
