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
import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.scope.DirectScopeKey;
import org.dockbox.hartshorn.component.ScopeKey;
import org.dockbox.hartshorn.component.Scoped;
import org.dockbox.hartshorn.inject.annotations.Binds;
import org.dockbox.hartshorn.component.processing.ComponentMemberType;
import org.dockbox.hartshorn.component.processing.CompositeMember;
import org.dockbox.hartshorn.inject.AutoConfiguringDependencyContext;
import org.dockbox.hartshorn.inject.ComponentInitializationException;
import org.dockbox.hartshorn.inject.ContextAwareComponentSupplier;
import org.dockbox.hartshorn.inject.DependencyContext;
import org.dockbox.hartshorn.inject.DependencyMap;
import org.dockbox.hartshorn.inject.annotations.Priority;
import org.dockbox.hartshorn.introspect.IntrospectionViewContextAdapter;
import org.dockbox.hartshorn.introspect.ViewContextAdapter;
import org.dockbox.hartshorn.util.ContextualInitializer;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.LazyStreamableConfigurer;
import org.dockbox.hartshorn.util.StreamableConfigurer;
import org.dockbox.hartshorn.util.TypeUtils;
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
                .orElseThrow(() -> new IllegalStateException("Method is not annotated with @Binds (or a compatible meta-annotation)"));

        return this.resolveInstanceBinding(strategyContext, strategyContext.method(), bindingDecorator, context.applicationContext());
    }

    private <T> DependencyContext<T> resolveInstanceBinding(BindingStrategyContext<?> context, AnnotatedGenericTypeView<T> declaration, Binds bindingDecorator, ApplicationContext applicationContext) {
        ComponentKey<T> componentKey = TypeUtils.adjustWildcards(this.environment.componentKeyResolver().resolve(declaration), ComponentKey.class);
        Set<ComponentKey<?>> dependencies = this.declarationDependencyResolver.dependencies(context);
        ContextAwareComponentSupplier<T> supplier = requestContext -> {
            try {
                ViewContextAdapter contextAdapter = new IntrospectionViewContextAdapter(applicationContext);
                contextAdapter.addContext(requestContext);
                return contextAdapter.load(declaration).orNull();
            }
            catch(Throwable throwable) {
                throw new ComponentInitializationException("Failed to obtain instance for " + declaration.qualifiedName(), throwable);
            }
        };

        return AutoConfiguringDependencyContext.builder(componentKey)
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

    private int resolvePriority(AnnotatedElementView view) {
        return view.annotations().get(Priority.class)
            .map(Priority::value)
            .orElse(Priority.DEFAULT_PRIORITY);
    }

    private ScopeKey resolveComponentScope(AnnotatedElementView view) {
        Option<Scoped> installToCandidate = view.annotations().get(Scoped.class);
        return installToCandidate.present()
                ? DirectScopeKey.of(installToCandidate.get().value())
                : ApplicationContext.APPLICATION_SCOPE;
    }

    private ComponentMemberType resolveMemberType(AnnotatedElementView bindsMethod) {
        return bindsMethod.annotations().has(CompositeMember.class)
            ? ComponentMemberType.COMPOSITE
            : ComponentMemberType.STANDALONE;
    }

    @Override
    public BindingStrategyPriority priority() {
        return BindingStrategyPriority.LOW;
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

    /**
     * TODO: #1060 Add documentation
     *
     * @since 0.5.0
     *
     * @author Guus Lieben
     */
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
