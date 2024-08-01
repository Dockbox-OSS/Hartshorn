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

package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.application.UseBootstrap;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.Configuration;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.component.processing.Singleton;
import org.dockbox.hartshorn.context.ConcreteContextCarrier;
import org.dockbox.hartshorn.context.ContextCarrier;

/**
 * Provides default bindings for the framework. These bindings are not required for the framework to function, but
 * are provided for convenience.
 *
 * @since 0.4.10
 *
 * @author Guus Lieben
 */
@Configuration
@RequiresActivator(UseBootstrap.class)
public class ContextConfiguration {

    /**
     * Provides a simple wrapper around the {@link ApplicationContext} to allow for easy access to the context in
     * components. This carrier should typically not be used directly, but may be used by proxies to delegate the
     * use of {@link ContextCarrier#applicationContext()} to the actual context.
     *
     * @param applicationContext the application context to wrap
     * @return a {@link ContextCarrier} wrapping the provided context
     */
    @Singleton
    @SupportPriority
    public ContextCarrier contextCarrier(ApplicationContext applicationContext) {
        return new ConcreteContextCarrier(applicationContext);
    }
}
