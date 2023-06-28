/*
 * Copyright 2019-2023 the original author or authors.
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
import org.dockbox.hartshorn.component.HierarchicalComponentProvider;
import org.dockbox.hartshorn.component.Scope;
import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.component.processing.ComponentProcessor;
import org.dockbox.hartshorn.context.ApplicationAwareContext;
import org.dockbox.hartshorn.logging.ApplicationLogger;
import org.dockbox.hartshorn.logging.LogExclude;
import org.dockbox.hartshorn.util.ApplicationException;
import org.slf4j.Logger;

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
 * @since 21.1
 * @author Guus Lieben
 */
@LogExclude
public interface ApplicationContext extends
        HierarchicalComponentProvider,
        ApplicationPropertyHolder,
        ApplicationAwareContext,
        ApplicationLogger,
        ExceptionHandler,
        ActivatorHolder,
        Scope,
        AutoCloseable {

    /**
     * Registers a component processor with the application context. The processor will be invoked when
     * a component is added to the application context.
     *
     * @param processor The component processor to register.
     * @see ComponentProcessor
     * @see org.dockbox.hartshorn.component.processing.ComponentPreProcessor
     * @see org.dockbox.hartshorn.component.processing.ComponentPostProcessor
     */
    void add(ComponentProcessor processor);

    void add(Class<? extends ComponentPostProcessor> processor);

    /**
     * Gets the active {@link ApplicationEnvironment} for the application.
     *
     * @return The active {@link ApplicationEnvironment} for the application.
     * @see ApplicationEnvironment
     */
    ApplicationEnvironment environment();

    @Override
    default Logger log() {
        return this.environment().log();
    }

    /**
     * Indicates whether the application context is closed. A closed context cannot be used reliably to
     * access its components, as the components may have been removed from the context, or have been
     * cancelled, interrupted, or otherwise stopped.
     *
     * @return {@code true} if the context is closed, {@code false} otherwise.
     */
    boolean isClosed();

    @Override
    default Class<? extends Scope> installableScopeType() {
        return ApplicationContext.class;
    }

    @Override
    void close() throws ApplicationException;
}
