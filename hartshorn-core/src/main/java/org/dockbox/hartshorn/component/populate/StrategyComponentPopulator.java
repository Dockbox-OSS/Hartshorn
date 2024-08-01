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

package org.dockbox.hartshorn.component.populate;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentPopulateException;
import org.dockbox.hartshorn.component.ComponentPopulator;
import org.dockbox.hartshorn.component.populate.inject.InjectPopulationStrategy;
import org.dockbox.hartshorn.context.ContextCarrier;
import org.dockbox.hartshorn.proxy.ProxyManager;
import org.dockbox.hartshorn.proxy.ProxyOrchestrator;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.ContextualInitializer;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.LazyStreamableConfigurer;
import org.dockbox.hartshorn.util.StreamableConfigurer;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.List;
import java.util.Set;

/**
 * A {@link ComponentPopulator} that populates components using a set of {@link ComponentPopulationStrategy}s. The
 * strategies are executed in the order they are provided to the constructor. If a strategy is applicable to a given
 * injection point, the strategy is executed. If the strategy is not applicable, the next strategy is executed.
 *
 * <p>Injection points are resolved using a {@link ComponentInjectionPointsResolver}. The resolver is expected to
 * return all injection points of a given type, without prior filtering. Filtering is expected to be done by the
 * {@link ComponentPopulationStrategy strategies}.
 *
 * @see ComponentPopulationStrategy
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public class StrategyComponentPopulator implements ComponentPopulator, ContextCarrier {

    private final List<ComponentPopulationStrategy> strategies;
    private final ApplicationContext applicationContext;

    public StrategyComponentPopulator(
            ApplicationContext applicationContext,
            List<ComponentPopulationStrategy> strategies
    ) {
        this.applicationContext = applicationContext;
        this.strategies = strategies;
    }

    @Override
    public <T> T populate(T instance) {
        if (null != instance) {
            T modifiableInstance = instance;
            ProxyOrchestrator orchestrator = this.applicationContext().environment().proxyOrchestrator();
            if(orchestrator.isProxy(instance)) {
                modifiableInstance = orchestrator
                        .manager(instance)
                        .flatMap(ProxyManager::delegate)
                        .orElse(modifiableInstance);
            }
            TypeView<T> typeView = this.applicationContext.environment().introspector().introspect(modifiableInstance);
            PopulateComponentContext<T> context = new PopulateComponentContext<>(
                    modifiableInstance,
                    instance,
                    typeView,
                    this.applicationContext
            );
            this.populate(context);
            return instance;
        }
        else {
            return null;
        }
    }

    protected <T> void populate(PopulateComponentContext<T> context) {
        TypeView<T> type = context.type();
        Set<ComponentInjectionPoint<T>> injectionPoints = this.applicationContext()
                .environment()
                .injectionPointsResolver()
                .resolve(type);

        for(ComponentPopulationStrategy strategy : this.strategies) {
            for(ComponentInjectionPoint<T> injectionPoint : injectionPoints) {
                try {
                    strategy.populate(context, injectionPoint);
                }
                catch(ApplicationException e) {
                    throw new ComponentPopulateException("Could not populate injection point " + injectionPoint.qualifiedName() + " in type " + type.qualifiedName(), e);
                }
            }
        }
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }

    public static ContextualInitializer<ApplicationContext, ComponentPopulator> create(Customizer<Configurer> customizer) {
        return context -> {
            Configurer configurer = new Configurer();
            customizer.configure(configurer);
            List<ComponentPopulationStrategy> populationStrategies = configurer.strategies.initialize(context);
            return new StrategyComponentPopulator(context.input(), List.copyOf(populationStrategies));
        };
    }

    /**
     * TODO: #1060 Add documentation
     *
     * @since 0.6.0
     *
     * @author Guus Lieben
     */
    public static class Configurer {

        private final LazyStreamableConfigurer<ApplicationContext, ComponentPopulationStrategy> strategies = LazyStreamableConfigurer.of(collection -> {
            collection.add(InjectPopulationStrategy.create(Customizer.useDefaults()));
        });

        public Configurer strategy(ComponentPopulationStrategy strategy) {
            this.strategies.customizer(collection -> collection.add(strategy));
            return this;
        }

        public Configurer strategies(Iterable<ComponentPopulationStrategy> strategies) {
            this.strategies.customizer(collection -> collection.addAll(strategies));
            return this;
        }

        public Configurer strategies(ComponentPopulationStrategy... strategies) {
            this.strategies.customizer(collection -> collection.addAll(strategies));
            return this;
        }

        public Configurer strategies(Customizer<StreamableConfigurer<ApplicationContext, ComponentPopulationStrategy>> customizer) {
            this.strategies.customizer(customizer);
            return this;
        }
    }
}
