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

import jakarta.inject.Singleton;
import java.util.Set;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.ComponentKey.ComponentKeyView;
import org.dockbox.hartshorn.component.DirectScopeKey;
import org.dockbox.hartshorn.component.InstallTo;
import org.dockbox.hartshorn.component.Scope;
import org.dockbox.hartshorn.component.ScopeKey;
import org.dockbox.hartshorn.component.processing.Binds;
import org.dockbox.hartshorn.component.processing.Binds.BindingType;
import org.dockbox.hartshorn.context.Context;
import org.dockbox.hartshorn.inject.AutoConfiguringDependencyContext;
import org.dockbox.hartshorn.inject.ComponentInitializationException;
import org.dockbox.hartshorn.inject.ComponentKeyCustomizerContext;
import org.dockbox.hartshorn.inject.DependencyContext;
import org.dockbox.hartshorn.inject.DependencyMap;
import org.dockbox.hartshorn.inject.ExactPriorityProviderSelectionStrategy;
import org.dockbox.hartshorn.inject.MaximumPriorityProviderSelectionStrategy;
import org.dockbox.hartshorn.inject.Priority;
import org.dockbox.hartshorn.introspect.IntrospectionViewContextAdapter;
import org.dockbox.hartshorn.introspect.ViewContextAdapter;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.function.CheckedSupplier;
import org.dockbox.hartshorn.util.introspect.view.AnnotatedElementView;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.option.Option;

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
        ScopeKey scope = this.resolveComponentScope(bindsMethod);
        int priority = bindingDecorator.priority();

        boolean lazy = bindingDecorator.lazy();
        boolean singleton = this.isSingleton(applicationContext, bindsMethod, componentKey);
        boolean processAfterInitialization = bindingDecorator.processAfterInitialization();
        BindingType bindingType = bindingDecorator.type();

        DependencyMap dependenciesMap = DependencyMap.create().immediate(dependencies);

        ViewContextAdapter contextAdapter = new IntrospectionViewContextAdapter(applicationContext);
        boolean hasSelfDependency = dependenciesMap.containsValue(componentKey);
        if (hasSelfDependency) {
            ComponentKeyCustomizerContext customizerContext = new ComponentKeyCustomizerContext(
                (context, key) -> configureParameterPriority(context, key, componentKey, priority)
            );
            contextAdapter.add(customizerContext);
        }
        CheckedSupplier<T> supplier = () -> contextAdapter.load(bindsMethod)
                .mapError(error -> new ComponentInitializationException("Failed to obtain instance for " + bindsMethod.qualifiedName(), error))
                .rethrow()
                .orNull();

        return new AutoConfiguringDependencyContext<>(componentKey, dependenciesMap, scope, priority, bindingType, bindsMethod, supplier)
                .lazy(lazy)
                .singleton(singleton)
                .processAfterInitialization(processAfterInitialization);
    }

    private static <T> void configureParameterPriority(Context context, ComponentKey.Builder<?> key, ComponentKey<T> componentKey, int priority) {
        if (context instanceof AnnotatedElementView annotatedElementView) {
            Option<Priority> priorityOption = annotatedElementView.annotations().get(Priority.class);
            if (priorityOption.present()) {
                key.strategy(new ExactPriorityProviderSelectionStrategy(priorityOption.get().value()));
                return;
            }
        }

        ComponentKeyView<?> view = key.view();
        if (view.matches(componentKey)) {
            key.strategy(new MaximumPriorityProviderSelectionStrategy(priority));
        }
    }

    private boolean isSingleton(ApplicationContext applicationContext, MethodView<?, ?> methodView,
                                ComponentKey<?> componentKey) {
        return methodView.annotations().has(Singleton.class)
                || applicationContext.environment().singleton(componentKey.type());
    }

    private ScopeKey resolveComponentScope(MethodView<?, ?> bindsMethod) {
        Option<InstallTo> installToCandidate = bindsMethod.annotations().get(InstallTo.class);
        return installToCandidate.present()
                ? DirectScopeKey.of(installToCandidate.get().value())
                : Scope.DEFAULT_SCOPE.installableScopeType();
    }

    private <T> ComponentKey<T> constructInstanceComponentKey(MethodView<?, T> bindsMethod, Binds bindingDecorator) {
        ComponentKey.Builder<T> keyBuilder = ComponentKey.builder(bindsMethod.genericReturnType());
        if (StringUtilities.notEmpty(bindingDecorator.value())) {
            keyBuilder = keyBuilder.name(bindingDecorator.value());
        }
        return keyBuilder.build();
    }
}
