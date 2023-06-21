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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.condition.ConditionMatcher;
import org.dockbox.hartshorn.component.processing.Binds;
import org.dockbox.hartshorn.inject.strategy.BindingStrategyContext;
import org.dockbox.hartshorn.inject.strategy.BindingStrategyRegistry;
import org.dockbox.hartshorn.inject.strategy.MethodAwareBindingStrategyContext;
import org.dockbox.hartshorn.inject.strategy.SimpleBindingStrategyRegistry;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BindsMethodDependencyResolver extends AbstractContainerDependencyResolver {

    private final ConditionMatcher conditionMatcher;
    private final BindingStrategyRegistry registry;

    public BindsMethodDependencyResolver(final ConditionMatcher conditionMatcher) {
        this(conditionMatcher, new SimpleBindingStrategyRegistry());
    }

    public BindsMethodDependencyResolver(final ConditionMatcher conditionMatcher, final BindingStrategyRegistry registry) {
        this.conditionMatcher = conditionMatcher;
        this.registry = registry;
    }

    public BindingStrategyRegistry registry() {
        return registry;
    }

    @Override
    protected <T> Set<DependencyContext<?>> resolveSingle(final ComponentContainer<T> componentContainer, final ApplicationContext applicationContext) {
        final TypeView<T> componentType = componentContainer.type();
        final List<? extends MethodView<T, ?>> bindsMethods = componentType.methods().annotatedWith(Binds.class);
        return bindsMethods.stream()
                .filter(this.conditionMatcher::match)
                .flatMap(bindsMethod -> this.resolve(applicationContext, componentContainer, bindsMethod).stream())
                .collect(Collectors.toSet());
    }

    private <T> Option<DependencyContext<?>> resolve(final ApplicationContext applicationContext, final ComponentContainer<T> componentContainer, final MethodView<T, ?> method) {
        final BindingStrategyContext<T> strategyContext = new MethodAwareBindingStrategyContext<>(applicationContext, componentContainer, method);
        return this.registry.find(strategyContext).map(strategy -> strategy.handle(strategyContext));
    }
}
