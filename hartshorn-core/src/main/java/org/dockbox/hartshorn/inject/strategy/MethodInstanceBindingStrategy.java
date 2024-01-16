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

package org.dockbox.hartshorn.inject.strategy;

import java.util.List;
import java.util.Set;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.DirectScopeKey;
import org.dockbox.hartshorn.component.InstallTo;
import org.dockbox.hartshorn.component.Scope;
import org.dockbox.hartshorn.component.ScopeKey;
import org.dockbox.hartshorn.component.processing.Binds;
import org.dockbox.hartshorn.component.processing.Binds.BindingType;
import org.dockbox.hartshorn.inject.AutoConfiguringDependencyContext;
import org.dockbox.hartshorn.inject.ComponentInitializationException;
import org.dockbox.hartshorn.inject.DependencyContext;
import org.dockbox.hartshorn.inject.DependencyMap;
import org.dockbox.hartshorn.inject.Priority;
import org.dockbox.hartshorn.introspect.IntrospectionViewContextAdapter;
import org.dockbox.hartshorn.introspect.ViewContextAdapter;
import org.dockbox.hartshorn.util.ContextualInitializer;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.LazyStreamableConfigurer;
import org.dockbox.hartshorn.util.StreamableConfigurer;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.function.CheckedSupplier;
import org.dockbox.hartshorn.util.introspect.view.AnnotatedElementView;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.option.Option;

import jakarta.inject.Singleton;

public class MethodInstanceBindingStrategy implements BindingStrategy {

    private final ApplicationEnvironment environment;
    private final BindingDeclarationDependencyResolver declarationDependencyResolver;

    public MethodInstanceBindingStrategy(ApplicationEnvironment environment, BindingDeclarationDependencyResolver declarationDependencyResolver) {
        this.environment = environment;
        this.declarationDependencyResolver = declarationDependencyResolver;
    }

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

        return this.resolveInstanceBinding(strategyContext, strategyContext.method(), bindingDecorator, context.applicationContext());
    }

    @Override
    public BindingStrategyPriority priority() {
        return BindingStrategyPriority.LOW;
    }

    private <T> DependencyContext<T> resolveInstanceBinding(BindingStrategyContext<?> context, MethodView<?, T> bindsMethod, Binds bindingDecorator, ApplicationContext applicationContext) {
        ComponentKey<T> componentKey = TypeUtils.adjustWildcards(this.environment.componentKeyResolver().resolve(bindsMethod), ComponentKey.class);
        Set<ComponentKey<?>> dependencies = this.declarationDependencyResolver.dependencies(context);
        ScopeKey scope = this.resolveComponentScope(bindsMethod);
        int priority = this.resolvePriority(bindsMethod);

        boolean lazy = bindingDecorator.lazy();
        boolean singleton = this.isSingleton(applicationContext, bindsMethod, componentKey);
        boolean processAfterInitialization = bindingDecorator.processAfterInitialization();
        BindingType bindingType = bindingDecorator.type();

        DependencyMap dependenciesMap = DependencyMap.create().immediate(dependencies);

        ViewContextAdapter contextAdapter = new IntrospectionViewContextAdapter(applicationContext);
        CheckedSupplier<T> supplier = () -> {
            try {
                return contextAdapter.load(bindsMethod).orNull();
            }
            catch(Throwable throwable) {
                throw new ComponentInitializationException("Failed to obtain instance for " + bindsMethod.qualifiedName(), throwable);
            }
        };

        return new AutoConfiguringDependencyContext<>(
                componentKey,
                dependenciesMap,
                scope,
                priority,
                bindingType,
                bindsMethod,
                supplier
        ).lazy(lazy)
                .singleton(singleton)
                .processAfterInitialization(processAfterInitialization);
    }

    private int resolvePriority(AnnotatedElementView view) {
        return view.annotations().get(Priority.class)
            .map(Priority::value)
            .orElse(Priority.DEFAULT_PRIORITY);
    }

    private boolean isSingleton(ApplicationContext applicationContext, AnnotatedElementView view, ComponentKey<?> componentKey) {
        return view.annotations().has(Singleton.class)
                || applicationContext.environment().singleton(componentKey.type());
    }

    private ScopeKey resolveComponentScope(AnnotatedElementView view) {
        Option<InstallTo> installToCandidate = view.annotations().get(InstallTo.class);
        return installToCandidate.present()
                ? DirectScopeKey.of(installToCandidate.get().value())
                : Scope.DEFAULT_SCOPE.installableScopeType();
    }

    public static ContextualInitializer<ApplicationContext, BindingStrategy> create(Customizer<Configurer> customizer) {
        return context -> {
            Configurer configurer = new Configurer();
            customizer.configure(configurer);

            List<BindingDeclarationDependencyResolver> dependencyResolvers = configurer.declarationDependencyResolvers.initialize(context);
            BindingDeclarationDependencyResolver resolver = new CompositeBindingDependencyResolver(Set.copyOf(dependencyResolvers));

            return new MethodInstanceBindingStrategy(context.input().environment(), resolver);
        };
    }

    public static class Configurer {

        private final LazyStreamableConfigurer<ApplicationContext, BindingDeclarationDependencyResolver> declarationDependencyResolvers = LazyStreamableConfigurer.of(resolvers -> {
            resolvers.add(ContextualInitializer.of(context -> new IntrospectionBindingDependencyResolver(context.environment())));
            resolvers.add(new BindingAfterDeclarationDependencyResolver());
        });

        public Configurer declarationDependencyResolvers(Customizer<StreamableConfigurer<ApplicationContext, BindingDeclarationDependencyResolver>> customizer) {
            this.declarationDependencyResolvers.customizer(customizer);
            return this;
        }
    }
}
