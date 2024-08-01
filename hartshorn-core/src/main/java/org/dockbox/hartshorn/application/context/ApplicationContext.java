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

package org.dockbox.hartshorn.application.context;

import org.dockbox.hartshorn.application.ActivatorHolder;
import org.dockbox.hartshorn.application.ApplicationBuilder;
import org.dockbox.hartshorn.application.ApplicationPropertyHolder;
import org.dockbox.hartshorn.application.ExceptionHandler;
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.component.DirectScopeKey;
import org.dockbox.hartshorn.component.HierarchicalComponentProvider;
import org.dockbox.hartshorn.component.Scope;
import org.dockbox.hartshorn.component.ScopeKey;
import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.component.processing.ComponentProcessor;
import org.dockbox.hartshorn.context.ApplicationAwareContext;
import org.dockbox.hartshorn.util.ApplicationException;

/**
 * The primary context for an application. This context is responsible for providing the application
 * environment, including components, global contexts, and properties. It also provides the ability
 * to log messages and exceptions in a standardized way.
 *
 * <p>The primary context is created by the {@link ApplicationBuilder}
 * and is accessible through any {@link org.dockbox.hartshorn.context.ContextCarrier} that is attached
 * to the application. If the context supports component injection, it can also be accessed through
 * any form of injection.
 *
 * <p>The application context is responsible for managing the lifecycle of the application, including
 * its components. The implementation may choose to perform specific actions based on available
 * activators and properties.
 *
 * @since 0.4.11
 * @author Guus Lieben
 */
public interface ApplicationContext extends
        HierarchicalComponentProvider,
        ApplicationPropertyHolder,
        ApplicationAwareContext,
        ExceptionHandler,
        ActivatorHolder,
        Scope,
        AutoCloseable {

    /**
     * The scope key for the application context. This key is used to register the application context
     * as a global scope.
     */
    ScopeKey APPLICATION_SCOPE = DirectScopeKey.of(ApplicationContext.class);

    /**
     * Registers a component processor with the application context. The processor will be invoked when
     * a component is loaded by the application context.
     *
     * @param processor The component processor to register.
     * @see ComponentProcessor
     * @see org.dockbox.hartshorn.component.processing.ComponentPreProcessor
     * @see org.dockbox.hartshorn.component.processing.ComponentPostProcessor
     */
    void add(ComponentProcessor processor);

    /**
     * Registers a lazy-loaded component post-processor with the application context. The processor will be instantiated
     * when it is first used. The processor will be invoked when a component is loaded by the application context.
     *
     * @param processor The component processor to register.
     * @see ComponentProcessor
     * @see org.dockbox.hartshorn.component.processing.ComponentPostProcessor
     */
    void add(Class<? extends ComponentPostProcessor> processor);

    /**
     * Gets the active {@link ApplicationEnvironment} for the application.
     *
     * @return The active {@link ApplicationEnvironment} for the application.
     * @see ApplicationEnvironment
     */
    ApplicationEnvironment environment();

    /**
     * Indicates whether the application context is closed. A closed context cannot be used reliably to
     * access its components, as the components may have been removed from the context, or have been
     * cancelled, interrupted, or otherwise stopped.
     *
     * @return {@code true} if the context is closed, {@code false} otherwise.
     */
    boolean isClosed();

    @Override
    default ScopeKey installableScopeType() {
        return APPLICATION_SCOPE;
    }

    @Override
    default Scope scope() {
        return this;
    }

    @Override
    void close() throws ApplicationException;
}
