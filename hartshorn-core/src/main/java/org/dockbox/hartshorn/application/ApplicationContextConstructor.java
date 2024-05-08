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

/**
 * A functional provider for {@link ApplicationContext} instances. This interface is often used by {@link ApplicationBuilder}
 * implementations to create the {@link ApplicationContext} instance. Implementations may decide whether to always return
 * the same instance, or create a new instance on each invocation.
 *
 * @see StandardApplicationContextConstructor
 *
 * @since 0.4.9
 *
 * @author Guus Lieben
 */
@FunctionalInterface
public interface ApplicationContextConstructor {

    /**
     * Returns a {@link ApplicationContext} instance. Depending on the implementation of this method, this may be a new
     * instance on each invocation, or always the same instance.
     *
     * <p>The returned instance is expected to be fully initialized, and ready for use. Implementations are expected to
     * perform all required initialization steps, such as the creation of components, and the activation of components.
     *
     * @return The {@link ApplicationContext} instance.
     */
    ApplicationContext createContext();
}
