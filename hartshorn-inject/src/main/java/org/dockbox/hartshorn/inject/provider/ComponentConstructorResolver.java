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

package org.dockbox.hartshorn.inject.provider;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.InjectorConfiguration;
import org.dockbox.hartshorn.inject.InjectorEnvironment;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.inject.binding.HierarchyLookup;
import org.dockbox.hartshorn.inject.graph.TypePathNode;
import org.dockbox.hartshorn.inject.targets.ComponentInjectionPointsResolver;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.List;

/**
 * A resolver for component constructors, which is responsible for finding the optimal constructor
 * for a given component type. The optimal constructor is the one with the highest number of
 * injectable parameters, allowing for the most dependencies to be satisfied at once.
 *
 * <p>If a component has no explicitly defined injectable constructor, this resolver will attempt to
 * find a
 * default constructor or, if configured to do so, fallback to a single constructor if only one
 * constructor is available.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public final class ComponentConstructorResolver {

    private final ComponentInjectionPointsResolver injectionPointsResolver;
    private final Introspector introspector;
    private final HierarchyLookup hierarchyLookup;
    private final InjectorConfiguration configuration;

    private ComponentConstructorResolver(
        ComponentInjectionPointsResolver injectionPointsResolver,
        Introspector introspector,
        HierarchyLookup hierarchyLookup,
        InjectorConfiguration configuration
    ) {
        this.injectionPointsResolver = injectionPointsResolver;
        this.introspector = introspector;
        this.hierarchyLookup = hierarchyLookup;
        this.configuration = configuration;
    }

    /**
     * Creates a new {@link ComponentConstructorResolver} with the given environment and hierarchy
     * lookup. The required {@link ComponentInjectionPointsResolver}, {@link Introspector}, and
     * {@link InjectorConfiguration} are obtained from the provided environment.
     *
     * @param environment the injector environment
     * @param hierarchyLookup the hierarchy lookup
     *
     * @return a new component constructor resolver
     */
    public static ComponentConstructorResolver create(
        InjectorEnvironment environment,
        HierarchyLookup hierarchyLookup
    ) {
        return new ComponentConstructorResolver(
            environment.injectionPointsResolver(),
            environment.introspector(),
            hierarchyLookup,
            environment.configuration()
        );
    }

    /**
     * Creates a new {@link ComponentConstructorResolver} using the provided application context.
     * The required {@link InjectorEnvironment} and {@link HierarchyLookup} are obtained from the
     * application context.
     *
     * @param applicationContext the injection-capable application context
     *
     * @return a new component constructor resolver
     */
    public static ComponentConstructorResolver create(
        InjectionCapableApplication applicationContext
    ) {
        return create(applicationContext.environment(), applicationContext.defaultBinder());
    }

    /**
     * Finds the optimal constructor for the given component type. The optimal constructor is the
     * one with the highest number of injectable parameters. If multiple constructors with the same
     * number of injectable parameters exist, the first one found will be returned.
     *
     * @param type the component type
     * @param <C> the component type
     *
     * @return an option containing the optimal constructor, or empty if no suitable constructor is
     * found
     *
     * @throws MissingInjectConstructorException if no injectable constructor is found for the given
     * type
     * @throws NoSuchProviderException if no suitable provider is found for the given type
     */
    public <C> Option<ConstructorView<? extends C>> findConstructor(TypeView<C> type)
        throws MissingInjectConstructorException, NoSuchProviderException {
        TypePathNode<C> node = new TypePathNode<>(type, ComponentKey.of(type), type);
        return this.findConstructor(node);
    }

    /**
     * Finds the optimal constructor for the given component type path node. The optimal constructor
     * is the one with the highest number of injectable parameters. If multiple constructors with
     * the same number of injectable parameters exist, the first one found will be returned.
     *
     * @param node the component type path node
     * @param <C> the component type
     *
     * @return an option containing the optimal constructor, or empty if no suitable constructor is
     * found
     *
     * @throws MissingInjectConstructorException if no injectable constructor is found for the given
     * type
     * @throws NoSuchProviderException if no suitable provider is found for the given type
     */
    public <C> Option<ConstructorView<? extends C>> findConstructor(TypePathNode<C> node)
        throws MissingInjectConstructorException, NoSuchProviderException {
        BindingHierarchy<C> hierarchy = this.hierarchyLookup.hierarchy(node.componentKey());
        int highestPriority = hierarchy.highestPriority();
        Option<InstantiationStrategy<C>> providerOption = hierarchy.get(highestPriority);
        return providerOption.absent()
            ? this.findConstructorInImplementation(node.type())
            : this.findConstructorInHierarchy(node, providerOption);
    }

    private <C> Option<ConstructorView<? extends C>> findConstructorInHierarchy(
        TypePathNode<C> node,
        Option<InstantiationStrategy<C>> providerOption
    )
        throws NoSuchProviderException, MissingInjectConstructorException {
        InstantiationStrategy<C> strategy = providerOption.get();
        if (strategy instanceof CompositeInstantiationStrategy<C> composite) {
            strategy = composite.provider();
        }

        if (strategy instanceof TypeAwareInstantiationStrategy<C> typeAwareInstantiationStrategy) {
            TypeView<? extends C> typeView =
                this.introspector.introspect(typeAwareInstantiationStrategy.type());
            return this.findConstructorInImplementation(typeView);
        }
        throw new NoSuchProviderException(NoSuchProviderException.ProviderType.TYPE_AWARE,
            node.componentKey());
    }

    private <C> Option<ConstructorView<? extends C>> findConstructorInImplementation(
        TypeView<? extends C> type
    ) throws MissingInjectConstructorException {
        if (type.modifiers().isAbstract()) {
            return Option.empty();
        }

        ConstructorView<? extends C> optimalConstructor;
        List<? extends ConstructorView<? extends C>> constructors =
            this.findAvailableConstructors(type);
        if (constructors.isEmpty()) {
            throw new MissingInjectConstructorException(type);
        }

        // An optimal constructor is the one with the highest amount of injectable parameters, so as
        // many dependencies can be satiated at once.
        optimalConstructor = constructors.getFirst();
        for (ConstructorView<? extends C> constructor : constructors) {
            if (optimalConstructor.parameters().count() < constructor.parameters().count()) {
                optimalConstructor = constructor;
            }
        }

        return Option.of(optimalConstructor);
    }

    private <C> List<ConstructorView<C>> findAvailableConstructors(TypeView<C> type) {
        List<ConstructorView<C>> constructors = type.constructors().all().stream()
            .filter(this.injectionPointsResolver::isInjectable)
            .toList();
        if (constructors.isEmpty()) {
            Option<ConstructorView<C>> defaultConstructor =
                type.constructors().defaultConstructor();
            if (defaultConstructor.present()) {
                return List.of(defaultConstructor.get());
            }
            else if (this.configuration.allowFallbackToSingleConstructor()
                && type.constructors().count() == 1) {
                return List.of(type.constructors().all().getFirst());
            }
        }
        return constructors;
    }
}
