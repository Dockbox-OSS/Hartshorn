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

package org.dockbox.hartshorn.inject.strategy;

import java.util.Set;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.InstallTo;
import org.dockbox.hartshorn.component.Scope;
import org.dockbox.hartshorn.component.processing.Binds;
import org.dockbox.hartshorn.inject.AutoConfiguringDependencyContext;
import org.dockbox.hartshorn.inject.ComponentInitializationException;
import org.dockbox.hartshorn.inject.DependencyContext;
import org.dockbox.hartshorn.inject.DependencyMap;
import org.dockbox.hartshorn.introspect.IntrospectionViewContextAdapter;
import org.dockbox.hartshorn.introspect.ViewContextAdapter;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.function.CheckedSupplier;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.option.Option;

import jakarta.inject.Singleton;

public class MethodInstanceBindingStrategy implements BindingStrategy {

    @Override
    public <T> boolean canHandle(BindingStrategyContext<T> context) {
        return context instanceof MethodAwareBindingStrategyContext<T> methodAwareBindingStrategyContext
                && methodAwareBindingStrategyContext.method().annotations().has(Binds.class);
    }

    @Override
    public <T> DependencyContext<?> handle(BindingStrategyContext<T> context) {
        MethodAwareBindingStrategyContext<T> strategyContext = (MethodAwareBindingStrategyContext<T>) context;
        Binds bindingDecorator = strategyContext.method().annotations()
                .get(Binds.class)
                .orElseThrow(() -> new IllegalStateException("Method is not annotated with @Binds"));

        return this.resolveInstanceBinding(strategyContext.method(), bindingDecorator, context.applicationContext());
    }

    @Override
    public BindingStrategyPriority priority() {
        return BindingStrategyPriority.LOW;
    }

    private <T> DependencyContext<T> resolveInstanceBinding(MethodView<?, T> bindsMethod, Binds bindingDecorator, ApplicationContext applicationContext) {
        ComponentKey<T> componentKey = this.constructInstanceComponentKey(bindsMethod, bindingDecorator);
        Set<ComponentKey<?>> dependencies = DependencyResolverUtils.resolveDependencies(bindsMethod);
        Class<? extends Scope> scope = this.resolveComponentScope(bindsMethod);
        int priority = bindingDecorator.priority();

        ViewContextAdapter contextAdapter = new IntrospectionViewContextAdapter(applicationContext);
        CheckedSupplier<T> supplier = () -> contextAdapter.load(bindsMethod)
                .mapError(error -> new ComponentInitializationException("Failed to obtain instance for " + bindsMethod.qualifiedName(), error))
                .rethrow()
                .orNull();

        boolean lazy = bindingDecorator.lazy();
        boolean singleton = this.isSingleton(applicationContext, bindsMethod, componentKey);
        boolean processAfterInitialization = bindingDecorator.processAfterInitialization();

        DependencyMap dependenciesMap = DependencyMap.create().immediate(dependencies);

        return new AutoConfiguringDependencyContext<>(componentKey, dependenciesMap, scope, priority, supplier, bindsMethod)
                .lazy(lazy)
                .singleton(singleton)
                .processAfterInitialization(processAfterInitialization);
    }

    private boolean isSingleton(ApplicationContext applicationContext, MethodView<?, ?> methodView,
                                ComponentKey<?> componentKey) {
        return methodView.annotations().has(Singleton.class)
                || applicationContext.environment().singleton(componentKey.type());
    }

    private Class<? extends Scope> resolveComponentScope(MethodView<?, ?> bindsMethod) {
        Option<InstallTo> installToCandidate = bindsMethod.annotations().get(InstallTo.class);
        return installToCandidate.present()
                ? installToCandidate.get().value()
                : Scope.DEFAULT_SCOPE.installableScopeType();
    }

    private <T> ComponentKey<T> constructInstanceComponentKey(MethodView<?, T> bindsMethod, Binds bindingDecorator) {
        ComponentKey.Builder<T> keyBuilder = ComponentKey.builder(bindsMethod.returnType().type());
        if (StringUtilities.notEmpty(bindingDecorator.value())) {
            keyBuilder = keyBuilder.name(bindingDecorator.value());
        }
        return keyBuilder.build();
    }
}
