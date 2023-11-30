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

package org.dockbox.hartshorn.component.populate;

import java.util.List;
import java.util.Set;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentPopulateException;
import org.dockbox.hartshorn.component.ComponentPopulator;
import org.dockbox.hartshorn.component.populate.context.ContextPopulationStrategy;
import org.dockbox.hartshorn.component.populate.inject.InjectPopulationStrategy;
import org.dockbox.hartshorn.context.ContextCarrier;
import org.dockbox.hartshorn.proxy.ProxyManager;
import org.dockbox.hartshorn.proxy.ProxyOrchestrator;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.ContextualInitializer;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.LazyStreamableConfigurer;
import org.dockbox.hartshorn.util.StreamableConfigurer;
import org.dockbox.hartshorn.util.introspect.view.AnnotatedElementView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

public class StrategyComponentPopulator implements ComponentPopulator, ContextCarrier {

    private final Set<ComponentPopulationStrategy> strategies;
    private final ApplicationContext applicationContext;
    private final ComponentInjectionPointsResolver injectionPointsResolver;

    public StrategyComponentPopulator(
            ApplicationContext applicationContext,
            Set<ComponentPopulationStrategy> strategies,
            ComponentInjectionPointsResolver injectionPointsResolver
    ) {
        this.applicationContext = applicationContext;
        this.strategies = strategies;
        this.injectionPointsResolver = injectionPointsResolver;
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
                    applicationContext
            );
            this.populate(context);
            return instance;
        }
        else {
            return null;
        }
    }

    private <T> void populate(PopulateComponentContext<T> context) {
        TypeView<T> type = context.type();
        Set<AnnotatedElementView> injectionPoints = injectionPointsResolver.resolve(type);

        T instance = context.instance();
        for(ComponentPopulationStrategy strategy : this.strategies) {
            for(AnnotatedElementView injectionPoint : injectionPoints) {
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
            ComponentInjectionPointsResolver resolver = configurer.injectionPointsResolver.initialize(context);
            return new StrategyComponentPopulator(context.input(), Set.copyOf(populationStrategies), resolver);
        };
    }

    public static class Configurer {

        private final LazyStreamableConfigurer<ApplicationContext, ComponentPopulationStrategy> strategies = LazyStreamableConfigurer.<ApplicationContext, ComponentPopulationStrategy>empty().customizer(collection -> {
            collection.add(InjectPopulationStrategy.create(Customizer.useDefaults()));
            collection.add(ContextPopulationStrategy.create(Customizer.useDefaults()));
        });
        private ContextualInitializer<ApplicationContext, ComponentInjectionPointsResolver> injectionPointsResolver = ContextualInitializer.of(MethodsAndFieldsInjectionPointResolver::new);

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

        public Configurer injectionPointsResolver(ComponentInjectionPointsResolver injectionPointsResolver) {
            this.injectionPointsResolver = ContextualInitializer.of(() -> injectionPointsResolver);
            return this;
        }

        public Configurer injectionPointsResolver(ContextualInitializer<ApplicationContext, ComponentInjectionPointsResolver> injectionPointsResolver) {
            this.injectionPointsResolver = injectionPointsResolver;
            return this;
        }
    }
}
