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

import org.dockbox.hartshorn.application.DefaultBindingConfigurerContext;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.component.ComponentLocator;
import org.dockbox.hartshorn.component.condition.ConditionMatcher;
import org.dockbox.hartshorn.component.processing.Binds;
import org.dockbox.hartshorn.inject.strategy.BindingStrategyContext;
import org.dockbox.hartshorn.inject.strategy.BindingStrategyRegistry;
import org.dockbox.hartshorn.inject.strategy.MethodAwareBindingStrategyContext;
import org.dockbox.hartshorn.inject.strategy.MethodInstanceBindingStrategy;
import org.dockbox.hartshorn.inject.strategy.SimpleBindingStrategyRegistry;
import org.dockbox.hartshorn.util.ContextualInitializer;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BindsMethodDependencyResolver extends AbstractContainerDependencyResolver {

    private final ConditionMatcher conditionMatcher;
    private final BindingStrategyRegistry registry;
    private final ComponentLocator componentLocator;

    public BindsMethodDependencyResolver(ConditionMatcher conditionMatcher) {
        this(conditionMatcher, new SimpleBindingStrategyRegistry());
    }

    public BindsMethodDependencyResolver(ConditionMatcher conditionMatcher, BindingStrategyRegistry registry) {
        super(conditionMatcher.applicationContext());
        this.conditionMatcher = conditionMatcher;
        this.registry = registry;
        this.componentLocator = conditionMatcher.applicationContext().get(ComponentLocator.class);
    }

    public BindingStrategyRegistry registry() {
        return this.registry;
    }

    @Override
    protected <T> Set<DependencyContext<?>> resolveSingle(DependencyDeclarationContext<T> componentContainer, ApplicationContext applicationContext) {
        TypeView<T> componentType = componentContainer.type();
        List<? extends MethodView<T, ?>> bindsMethods = componentType.methods().annotatedWith(Binds.class);

        // Binds methods are only processed on managed components. If the component container is not present, there is nothing to do but check that there
        // is no incorrect usage of the @Binds annotation.
        if (this.componentLocator.container(componentType.type()).absent()) {
            if (!bindsMethods.isEmpty()) {
                throw new IllegalStateException("Component " + componentType.type().getName() + " is not a managed component, but contains @Binds methods.");
            }
            return Set.of();
        }
        else {
            return bindsMethods.stream()
                    .filter(this.conditionMatcher::match)
                    .flatMap(bindsMethod -> this.resolve(applicationContext, componentContainer, bindsMethod).stream())
                    .collect(Collectors.toSet());
        }
    }

    private <T> Option<DependencyContext<?>> resolve(ApplicationContext applicationContext, DependencyDeclarationContext<T> componentContainer, MethodView<T, ?> method) {
        BindingStrategyContext<T> strategyContext = new MethodAwareBindingStrategyContext<>(applicationContext, componentContainer, method);
        return this.registry.find(strategyContext).map(strategy -> strategy.handle(strategyContext));
    }

    public static ContextualInitializer<ApplicationContext, DependencyResolver> create(Customizer<Configurer> customizer) {
        return context -> {
            Configurer configurer = new Configurer();
            customizer.configure(configurer);

            BindingStrategyRegistry registry = new SimpleBindingStrategyRegistry();
            ApplicationContext applicationContext = context.input();

            ApplicationEnvironment environment = applicationContext.environment();
            registry.register(new MethodInstanceBindingStrategy(environment));

            configurer.registryCustomizer.configure(registry);

            ConditionMatcher conditionMatcher = configurer.conditionMatcher.initialize(context.transform(applicationContext));
            DefaultBindingConfigurerContext.compose(context, binder -> {
                binder.bind(ConditionMatcher.class).singleton(conditionMatcher);
            });

            return new BindsMethodDependencyResolver(conditionMatcher, registry);
        };
    }

    public static class Configurer {

        private ContextualInitializer<ApplicationContext, ConditionMatcher> conditionMatcher = context -> new ConditionMatcher(context.input());
        private Customizer<BindingStrategyRegistry> registryCustomizer = Customizer.useDefaults();

        public Configurer conditionMatcher(ConditionMatcher conditionMatcher) {
            return this.conditionMatcher(ContextualInitializer.of(conditionMatcher));
        }

        public Configurer conditionMatcher(ContextualInitializer<ApplicationContext, ConditionMatcher> conditionMatcher) {
            this.conditionMatcher = conditionMatcher;
            return this;
        }

        public Configurer registry(Customizer<BindingStrategyRegistry> customizer) {
            this.registryCustomizer = customizer;
            return this;
        }
    }
}
