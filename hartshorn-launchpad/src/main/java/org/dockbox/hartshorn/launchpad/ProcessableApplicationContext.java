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

package org.dockbox.hartshorn.launchpad;

import org.dockbox.hartshorn.inject.processing.ComponentPreProcessor;
import org.dockbox.hartshorn.inject.provider.PostProcessingComponentProvider;
import org.dockbox.hartshorn.util.IllegalModificationException;

/**
 * A {@link ProcessableApplicationContext} is an {@link ApplicationContext} that can be lazily loaded. This allows for
 * the {@link ApplicationContext} to be configured before it is loaded, which is useful when the {@link ApplicationContext}
 * requires information from the environment to be loaded.
 *
 * <p>A {@link ProcessableApplicationContext} also allows for {@link ComponentPreProcessor} instances to be registered
 * that will be executed before the {@link ApplicationContext} is loaded. This allows for the {@link ApplicationContext}
 * to prepare components before the application state is fully loaded.
 *
 * @see ApplicationContext
 *
 * @since 0.4.9
 *
 * @author Guus Lieben
 */
public interface ProcessableApplicationContext extends ApplicationContext {

    @Override // Expand type to PostProcessingComponentProvider, ensuring the processor registry is available
    PostProcessingComponentProvider defaultProvider();

    /**
     * Loads the context. This will cause the {@link ApplicationContext} to become active, and may cause
     * the {@link ApplicationContext} prevent further configuration. This method may only be called once.
     *
     * @throws IllegalModificationException when the {@link ApplicationContext} is already loaded
     */
    void loadContext();

}
