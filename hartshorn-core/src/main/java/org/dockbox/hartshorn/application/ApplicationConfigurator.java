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
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;

/**
 * The {@link ApplicationConfigurator} is responsible for configuring the {@link ApplicationEnvironment}. Configuration
 * actions can range from setting environment values to overriding internal components. Applications are configured
 * in the {@link ApplicationBuilder} after the environment and {@link ApplicationContext} have been created, but before
 * active bindings are added.
 *
 * @author Guus Lieben
 * @since 21.9
 */
public interface ApplicationConfigurator {

    /**
     * Early configuration actions performed after the {@link ApplicationEnvironment} has been created, but before the
     * active bindings are added.
     * @param environment The {@link ApplicationEnvironment} to configure.
     */
    void configure(ApplicationEnvironment environment);

    /**
     * Binds the given prefix to the given {@link ApplicationEnvironment}. Prefixes may be validated, modified, or applied
     * to the {@link ApplicationContext}.
     *
     * @param environment The {@link ApplicationEnvironment} to configure.
     * @param prefix The prefix to bind.
     */
    void bind(ApplicationEnvironment environment, String prefix);
}
