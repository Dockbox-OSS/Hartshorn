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

package org.dockbox.hartshorn.inject.graph.strategy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.annotations.configuration.BindingAlias;
import org.dockbox.hartshorn.inject.graph.AliasableConfigurableDependencyContext;
import org.dockbox.hartshorn.inject.graph.resolve.BindingAfterDeclarationDependencyResolver;
import org.dockbox.hartshorn.inject.graph.resolve.BindingDeclarationDependencyResolver;
import org.dockbox.hartshorn.inject.graph.resolve.CompositeBindingDependencyResolver;
import org.dockbox.hartshorn.inject.graph.resolve.IntrospectionBindingDependencyResolver;
import org.dockbox.hartshorn.inject.scope.ScopeKey;
import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.scope.DirectScopeKey;
import org.dockbox.hartshorn.inject.annotations.configuration.Scoped;
import org.dockbox.hartshorn.inject.annotations.configuration.Binds;
import org.dockbox.hartshorn.inject.graph.ComponentMemberType;
import org.dockbox.hartshorn.inject.annotations.CompositeMember;
import org.dockbox.hartshorn.inject.graph.support.ComponentInitializationException;
import org.dockbox.hartshorn.inject.provider.PrototypeInstantiationStrategy;
import org.dockbox.hartshorn.inject.graph.declaration.DependencyContext;
import org.dockbox.hartshorn.inject.graph.DependencyMap;
import org.dockbox.hartshorn.inject.annotations.Priority;
import org.dockbox.hartshorn.inject.introspect.InjectorApplicationViewAdapter;
import org.dockbox.hartshorn.inject.introspect.ViewContextAdapter;
import org.dockbox.hartshorn.util.configure.ContextualInitializer;
import org.dockbox.hartshorn.util.configure.Customizer;
import org.dockbox.hartshorn.util.configure.LazyStreamableConfigurer;
import org.dockbox.hartshorn.util.configure.StreamableConfigurer;
import org.dockbox.hartshorn.util.types.TypeUtils;
import org.dockbox.hartshorn.util.introspect.view.AnnotatedElementView;
import org.dockbox.hartshorn.util.introspect.view.AnnotatedGenericTypeView;
import org.dockbox.hartshorn.util.option.Option;

/**
 * TODO: #1060 Add documentation
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class MethodInstanceBindingStrategy implements BindingStrategy {

    private final InjectionCapableApplication application;
    private final BindingDeclarationDependencyResolver declarationDependencyResolver;

    public MethodInstanceBindingStrategy(InjectionCapableApplication application, BindingDeclarationDependencyResolver declarationDependencyResolver) {
        this.application = application;
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
                .orElseThrow(() -> new IllegalStateException("Method is not annotated with @Binds (or a compatible meta-annotation)"));

        return this.resolveInstanceBinding(strategyContext, strategyContext.method(), bindingDecorator, this.application);
    }

    private <T> DependencyContext<T> resolveInstanceBinding(BindingStrategyContext<?> context, AnnotatedGenericTypeView<T> declaration, Binds bindingDecorator, InjectionCapableApplication application) {
        ComponentKey<T> componentKey = TypeUtils.unchecked(this.application.environment().componentKeyResolver().resolve(declaration), ComponentKey.class);
        Set<ComponentKey<?>> dependencies = this.declarationDependencyResolver.dependencies(context);
        PrototypeInstantiationStrategy<T> supplier = this.getPrototypeInstantiationStrategy(declaration, application);

        return AliasableConfigurableDependencyContext.builder(componentKey)
                .aliasTypes(this.resolveAliasTypes(declaration, componentKey.type()))
                .dependencies(DependencyMap.create().immediate(dependencies))
                .scope(this.resolveComponentScope(declaration))
                .priority(this.resolvePriority(declaration))
                .memberType(this.resolveMemberType(declaration))
                .view(declaration)
                .supplier(supplier)
                .lazy(bindingDecorator.lazy())
                .lifecycleType(bindingDecorator.lifecycle())
                .processAfterInitialization(bindingDecorator.processAfterInitialization())
                .build();
    }

    private <T> PrototypeInstantiationStrategy<T> getPrototypeInstantiationStrategy(AnnotatedGenericTypeView<T> declaration, InjectionCapableApplication application) {
        return requestContext -> {
            try {
                ViewContextAdapter contextAdapter = new InjectorApplicationViewAdapter(application);
                contextAdapter.addContext(requestContext);
                return contextAdapter.load(declaration).orNull();
            } catch (Throwable throwable) {
                throw new ComponentInitializationException("Failed to obtain instance for " + declaration.qualifiedName(), throwable);
            }
        };
    }

    private int resolvePriority(AnnotatedElementView view) {
        return view.annotations().get(Priority.class)
            .map(Priority::value)
            .orElse(Priority.DEFAULT_PRIORITY);
    }

    private ScopeKey resolveComponentScope(AnnotatedElementView view) {
        Option<Scoped> installToCandidate = view.annotations().get(Scoped.class);
        return installToCandidate.present()
                ? DirectScopeKey.of(installToCandidate.get().value())
                : this.application.defaultProvider().scope().installableScopeType();
    }

    private ComponentMemberType resolveMemberType(AnnotatedElementView bindsMethod) {
        return bindsMethod.annotations().has(CompositeMember.class)
            ? ComponentMemberType.COMPOSITE
            : ComponentMemberType.STANDALONE;
    }

    private <T> Set<Class<? super T>> resolveAliasTypes(AnnotatedElementView view, Class<T> baseType) {
        Set<Class<? super T>> types = new HashSet<>();
        for (BindingAlias alias : view.annotations().all(BindingAlias.class)) {
            Class<?>[] aliasTypes = alias.value();
            for (Class<?> type : aliasTypes) {
                if (type.isAssignableFrom(baseType)) {
                    types.add((Class<? super T>) type);
                }
                else {
                    throw new IllegalStateException("Binding alias " + type.getSimpleName() + " is not assignable from binding type " + baseType.getSimpleName());
                }
            }
        }
        return types;
    }

    @Override
    public BindingStrategyPriority priority() {
        return BindingStrategyPriority.LOW;
    }

    public static ContextualInitializer<InjectionCapableApplication, BindingStrategy> create(Customizer<Configurer> customizer) {
        return context -> {
            Configurer configurer = new Configurer();
            customizer.configure(configurer);

            List<BindingDeclarationDependencyResolver> dependencyResolvers = configurer.declarationDependencyResolvers.initialize(context);
            BindingDeclarationDependencyResolver resolver = new CompositeBindingDependencyResolver(Set.copyOf(dependencyResolvers));

            return new MethodInstanceBindingStrategy(context.input(), resolver);
        };
    }

    /**
     * TODO: #1060 Add documentation
     *
     * @since 0.5.0
     *
     * @author Guus Lieben
     */
    public static class Configurer {

        private final LazyStreamableConfigurer<InjectionCapableApplication, BindingDeclarationDependencyResolver> declarationDependencyResolvers = LazyStreamableConfigurer.of(resolvers -> {
            resolvers.add(ContextualInitializer.of(context -> new IntrospectionBindingDependencyResolver(
                    context.environment().injectionPointsResolver(),
                    context.environment().componentKeyResolver()
            )));
            resolvers.add(new BindingAfterDeclarationDependencyResolver());
        });

        public Configurer declarationDependencyResolvers(Customizer<StreamableConfigurer<InjectionCapableApplication, BindingDeclarationDependencyResolver>> customizer) {
            this.declarationDependencyResolvers.customizer(customizer);
            return this;
        }
    }
}
