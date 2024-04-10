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

package org.dockbox.hartshorn.component.processing;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.ComponentRegistry;
import org.dockbox.hartshorn.component.IllegalComponentModificationException;
import org.dockbox.hartshorn.proxy.Proxy;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.TypeUtils;

/**
 * A component post processor is responsible for processing a component after it has been created. This
 * can be used to add additional functionality to a component, or to modify the component in some way.
 *
 * <p>The component post processor will only process a component if it is known to the application, which
 * is validated through the active {@link ComponentRegistry}. If no {@link ComponentContainer} exists for
 * the component, the component post processor will not be called.
 *
 * <p>To specify when a component post processor should be active, and when the application should ignore
 * it, the processor will always require an activator annotation to be specified as type parameter
 * {@code A}. If the specified annotation is not annotated with {@link ServiceActivator}, it is up to
 * the active {@link ApplicationContext} to decide whether this should be considered an error or not.
 *
 * <p>The component post processor will be called for each component that is created, and will be called
 * in the order of the specified {@link OrderedComponentProcessor#priority()} value.
 *
 * @author Guus Lieben
 * @since 0.4.9
 */
public abstract non-sealed class ComponentPostProcessor implements ComponentProcessor {

    /**
     * Checks if the specified context can be processed by this component post processor. By default,
     * this method will always return {@code true}. Subclasses can override this method to provide
     * additional validation.
     *
     * @param processingContext The context to be processed
     * @return {@code true} if the context can be processed, {@code false} otherwise
     * @param <T> The type of the component to be processed
     */
    public <T> boolean isCompatible(ComponentProcessingContext<T> processingContext) {
        return true;
    }

    @Override
    public final <T> T process(ComponentProcessingContext<T> processingContext) throws ApplicationException {
        T instance = processingContext.instance();

        if (!this.isCompatible(processingContext)) {
            return instance;
        }

        this.preConfigureComponent(processingContext.applicationContext(), processingContext.instance(), processingContext);
        checkForModification(processingContext, this, instance, processingContext.instance());

        T updatedInstance = this.initializeComponent(processingContext.applicationContext(), processingContext.instance(), processingContext);
        if (processingContext instanceof ModifiableComponentProcessingContext<T> modifiableComponentProcessingContext) {
            if (!modifiableComponentProcessingContext.isInstanceLocked()) {
                modifiableComponentProcessingContext.instance(updatedInstance);
            }
            else if (updatedInstance != modifiableComponentProcessingContext.instance()) {
                throw new IllegalComponentModificationException(processingContext.key().type().getSimpleName(), this.priority(), this);
            }
        }

        this.postConfigureComponent(processingContext.applicationContext(), processingContext.instance(), processingContext);
        checkForModification(processingContext, this, updatedInstance, processingContext.instance());

        return updatedInstance;

    }

    /**
     * Early configuration of the component. This method is called before the component is
     * initialized. Early configuration can be used to configure context that is required for the
     * initialization of the component, but is not available in the component itself.
     *
     * @param context the application context
     * @param instance the component instance
     * @param processingContext the processing context
     * @param <T> the type of the component
     */
    public <T> void preConfigureComponent(ApplicationContext context, @Nullable T instance, ComponentProcessingContext<T> processingContext) throws ApplicationException {
        // Do nothing by default
    }

    /**
     * Initializes the component. This method is called in order to initialize the component. The
     * provided instance is the component instance that was created by the application. The returned
     * instance is the component instance that will be used by the application.
     *
     * @param context the application context
     * @param instance the component instance
     * @param processingContext the processing context
     * @return the initialized component
     * @param <T> the type of the component
     */
    public <T> T initializeComponent(ApplicationContext context, @Nullable T instance, ComponentProcessingContext<T> processingContext) throws ApplicationException {
        // Do nothing by default
        return instance;
    }

    /**
     * Late configuration of the component. This method is called after the component is
     * initialized. Late configuration can be used to configure context present in the component
     * itself. For example, this method can be used to configure a component that implements
     * specific interfaces or to populate properties.
     *
     * @param context the application context
     * @param instance the component instance
     * @param processingContext the processing context
     * @param <T> the type of the component
     */
    public <T> void postConfigureComponent(ApplicationContext context, @Nullable T instance, ComponentProcessingContext<T> processingContext) throws ApplicationException {
        // Do nothing by default
    }

    /**
     * Processes the specified component. The component is guaranteed to be known to the application.
     *
     * @param context the application context
     * @param instance the component instance
     * @param processingContext the processing context
     * @return the processed component
     * @param <T> the type of the component
     *
     * @deprecated use {@link #preConfigureComponent(ApplicationContext, Object, ComponentProcessingContext)},
     *             {@link #initializeComponent(ApplicationContext, Object, ComponentProcessingContext)} and
     *             {@link #postConfigureComponent(ApplicationContext, Object, ComponentProcessingContext)} instead
     *             of this method
     */
    @Deprecated(forRemoval = true, since = "0.5.0")
    public <T> T process(ApplicationContext context, @Nullable T instance, ComponentProcessingContext<T> processingContext) {
        throw new UnsupportedOperationException("This method is deprecated, use preConfigureComponent, initializeComponent and postConfigureComponent instead");
    }

    /**
     * Checks if the specified component has been modified. If the component has been modified, an
     * {@link IllegalComponentModificationException} is thrown. This method is called after each
     * processing step which does not allow modification of the component.
     *
     * @param processingContext the processing context
     * @param postProcessor the component post processor
     * @param instance the original component instance
     * @param modified the modified component instance
     * @param <T> the type of the component
     */
    private static <T> void checkForModification(ComponentProcessingContext<T> processingContext,
                                                 ComponentPostProcessor postProcessor,
                                                 T instance, T modified) {
        if (instance != modified) {
            boolean ok = false;
            if (modified instanceof Proxy<?> modifiedProxy) {
                Proxy<T> proxy = TypeUtils.adjustWildcards(modifiedProxy, Proxy.class);
                ok = proxy.manager().delegate().orNull() == instance;
            }
            if (!ok) {
                throw new IllegalComponentModificationException(processingContext.key().type().getSimpleName(), postProcessor.priority(), postProcessor);
            }
            if (processingContext instanceof ModifiableComponentProcessingContext<T> modifiableComponentProcessingContext) {
                modifiableComponentProcessingContext.instance(modified);
            }
        }
    }
}
