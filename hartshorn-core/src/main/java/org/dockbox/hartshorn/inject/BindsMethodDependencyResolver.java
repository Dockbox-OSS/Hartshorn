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

package org.dockbox.hartshorn.inject;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.dockbox.hartshorn.application.context.ApplicationContext;
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

public class BindsMethodDependencyResolver extends AbstractContainerDependencyResolver {

    private final ConditionMatcher conditionMatcher;
    private final BindingStrategyRegistry registry;

    public BindsMethodDependencyResolver(ConditionMatcher conditionMatcher) {
        this(conditionMatcher, new SimpleBindingStrategyRegistry());
    }

    public BindsMethodDependencyResolver(ConditionMatcher conditionMatcher, BindingStrategyRegistry registry) {
        super(conditionMatcher.applicationContext());
        this.conditionMatcher = conditionMatcher;
        this.registry = registry;
    }

    public BindingStrategyRegistry registry() {
        return this.registry;
    }

    @Override
    protected <T> Set<DependencyContext<?>> resolveSingle(DependencyDeclarationContext<T> componentContainer, ApplicationContext applicationContext) {
        TypeView<T> componentType = componentContainer.type();
        List<? extends MethodView<T, ?>> bindsMethods = componentType.methods().annotatedWith(Binds.class);
        return bindsMethods.stream()
                .filter(this.conditionMatcher::match)
                .flatMap(bindsMethod -> this.resolve(applicationContext, componentContainer, bindsMethod).stream())
                .collect(Collectors.toSet());
    }

    private <T> Option<DependencyContext<?>> resolve(ApplicationContext applicationContext, DependencyDeclarationContext<T> componentContainer, MethodView<T, ?> method) {
        BindingStrategyContext<T> strategyContext = new MethodAwareBindingStrategyContext<>(applicationContext, componentContainer, method);
        return this.registry.find(strategyContext).map(strategy -> strategy.handle(strategyContext));
    }

    public static ContextualInitializer<ConditionMatcher, DependencyResolver> create(Customizer<BindingStrategyRegistry> customizer) {
        return context -> {
            BindingStrategyRegistry registry = new SimpleBindingStrategyRegistry();
            registry.register(new MethodInstanceBindingStrategy());

            customizer.configure(registry);
            return new BindsMethodDependencyResolver(context.input(), registry);
        };
    }
}
