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

package org.dockbox.hartshorn.inject.processing;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.binding.DefaultBindingConfigurerContext;
import org.dockbox.hartshorn.inject.introspect.ComponentExecutableInvocationAdapter;
import org.dockbox.hartshorn.inject.populate.ComponentPopulator;
import org.dockbox.hartshorn.inject.populate.StrategyComponentPopulator;
import org.dockbox.hartshorn.inject.provider.ComponentConstructorResolver;
import org.dockbox.hartshorn.inject.scope.Scope;
import org.dockbox.hartshorn.proxy.ProxyFactory;
import org.dockbox.hartshorn.proxy.lookup.StateAwareProxyFactory;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.ApplicationRuntimeException;
import org.dockbox.hartshorn.util.configure.ContextualInitializer;
import org.dockbox.hartshorn.util.configure.Customizer;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.Collection;

/**
 * A {@link ComponentPostProcessor} that populates components using a {@link ComponentPopulator}. If
 * no instance is available at the time of processing, an attempt is made to create a proxy instance
 * using the {@link ProxyFactory} stored in the
 * {@link ComponentProcessingContext processing context}.
 *
 * @author Guus Lieben
 * @since 0.4.11
 */
public class ComponentPopulatorPostProcessor extends ComponentPostProcessor {

    private final ComponentPopulator componentPopulator;

    public ComponentPopulatorPostProcessor(ComponentPopulator componentPopulator) {
        this.componentPopulator = componentPopulator;
    }

    @Override
    public <T> T initializeComponent(
        InjectionCapableApplication application,
        @Nullable T instance,
        ComponentProcessingContext<T> processingContext
    ) {
        boolean permitsProxying = this.permitsProxying(application, instance, processingContext);
        if (permitsProxying && !(instance instanceof Collection<?>)) {
            T finalizingInstance = instance;

            if (processingContext.containsKey(ProxyFactory.class)) {
                ProxyFactory<T> factory = processingContext.get(ProxyFactory.class);

                boolean isStateAwareFactory = factory instanceof StateAwareProxyFactory<?>;
                // If not state aware, always assume state has been modified
                boolean stateModified =
                    !isStateAwareFactory || ((StateAwareProxyFactory<T>) factory).modified();
                boolean noConcreteInstancePossible =
                    instance == null && processingContext.type().modifiers().isAbstract();
                try {
                    if (stateModified || noConcreteInstancePossible) {
                        finalizingInstance =
                            this.createProxyInstance(application, factory, instance);
                    }
                }
                catch (ApplicationException e) {
                    throw new ApplicationRuntimeException(e);
                }
            }

            if (finalizingInstance == null) {
                // If no instance is available, we cannot proceed with population, as there's nothing to populate
                return null;
            }

            if (processingContext instanceof LockableComponentProcessingContext<T> lockableComponentProcessingContext) {
                lockableComponentProcessingContext.instance(finalizingInstance);
                lockableComponentProcessingContext.requestInstanceLock();
            }

            Scope scope =
                processingContext.key().scope().orElse(application.defaultProvider().scope());
            return this.componentPopulator.populate(finalizingInstance, scope);
        }
        return instance;
    }

    /**
     * Determines whether proxying is permitted for the given component processing context.
     *
     * @param application the application that owns the context
     * @param instance the current instance, if any
     * @param processingContext the component processing context
     * @param <T> the type of the instance
     *
     * @return true if proxying is permitted, false otherwise
     */
    protected <T> boolean permitsProxying(
        InjectionCapableApplication application, @Nullable T instance,
        ComponentProcessingContext<T> processingContext
    ) {
        return processingContext.permitsProxying();
    }

    /**
     * Creates a proxy instance using the provided {@link ProxyFactory}. If the factory requires
     * constructor arguments, the optimal constructor is resolved through the
     * {@link ComponentConstructorResolver}, and the required arguments are loaded using the
     * {@link ComponentExecutableInvocationAdapter}. If no proxy can be created, the provided
     * instance is returned as-is.
     *
     * @param application the application that owns the context
     * @param factory the proxy factory
     * @param instance the existing instance, if any
     * @param <T> the type of the instance
     *
     * @return the created proxy instance, or the existing instance if no proxy could be created
     *
     * @throws ApplicationException if an error occurs during proxy creation
     */
    protected <T> T createProxyInstance(
        InjectionCapableApplication application,
        ProxyFactory<T> factory,
        @Nullable T instance
    ) throws ApplicationException {
        TypeView<T> factoryType =
            application.environment().introspector().introspect(factory.type());
        // Ensure we use a non-default constructor if there is no default constructor to use
        if (!factoryType.isInterface() && factoryType.constructors()
            .defaultConstructor()
            .absent()) {
            ConstructorView<? extends T> constructor =
                ComponentConstructorResolver.create(application).findConstructor(factoryType)
                    .orElseThrow(() -> new ApplicationException(
                        "No default or injectable constructor found for proxy factory "
                            + factoryType.name()));

            ComponentExecutableInvocationAdapter adapter =
                application.defaultProvider().get(ComponentExecutableInvocationAdapter.class);
            Object[] arguments = adapter.loadParameters(constructor);
            return factory.proxy(constructor, arguments).orElse(instance);
        }
        return factory.proxy().orElse(instance);
    }

    @Override
    public int priority() {
        // Run after all other core post processors, but permit external post processors to run after this one
        return ProcessingPriority.LOWEST_PRECEDENCE - 128;
    }

    /**
     * Creates a {@link ContextualInitializer} for the {@link ComponentPopulatorPostProcessor},
     * which can be customized using the provided {@link Customizer}.
     *
     * @param customizer the customizer for the configurer
     *
     * @return the contextual initializer
     */
    public static ContextualInitializer<InjectionCapableApplication, ComponentPostProcessor> create(
        Customizer<Configurer> customizer
    ) {
        return context -> {
            Configurer configurer = new Configurer();
            customizer.configure(configurer);
            ComponentPopulator populator = configurer.componentPopulator.initialize(context);
            DefaultBindingConfigurerContext.compose(context, binder -> {
                binder.bind(ComponentPopulator.class).singleton(populator);
            });
            return new ComponentPopulatorPostProcessor(populator);
        };
    }

    /**
     * Configurer for the {@link ComponentPopulatorPostProcessor}.
     *
     * @author Guus Lieben
     * @since 0.6.0
     */
    public static class Configurer {

        ContextualInitializer<InjectionCapableApplication, ComponentPopulator> componentPopulator =
            StrategyComponentPopulator.create(Customizer.useDefaults());

        /**
         * Sets the component populator to use.
         *
         * @param componentPopulator the component populator
         *
         * @return this configurer
         */
        public Configurer componentPopulator(ComponentPopulator componentPopulator) {
            return this.componentPopulator(ContextualInitializer.of(componentPopulator));
        }

        /**
         * Sets the component populator to use.
         *
         * @param componentPopulator the component populator initializer
         *
         * @return this configurer
         */
        public Configurer componentPopulator(ContextualInitializer<InjectionCapableApplication, ComponentPopulator> componentPopulator) {
            this.componentPopulator = componentPopulator;
            return this;
        }
    }
}
