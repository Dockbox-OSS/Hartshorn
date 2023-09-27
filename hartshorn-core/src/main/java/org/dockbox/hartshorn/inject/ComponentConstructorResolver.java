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
import java.util.Objects;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.inject.NoSuchProviderException.ProviderType;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Attempt;
import org.dockbox.hartshorn.util.option.Option;
import org.jetbrains.annotations.Nullable;

public final class ComponentConstructorResolver {

    private final ApplicationContext applicationContext;

    private ComponentConstructorResolver(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public static ComponentConstructorResolver create(ApplicationContext applicationContext) {
        return new ComponentConstructorResolver(applicationContext);
    }

    public <C> Attempt<ConstructorView<? extends C>, ? extends ApplicationException> findConstructor(TypeView<C> type) {
        TypePathNode<C> node = new TypePathNode<>(type, ComponentKey.of(type));
        return this.findConstructor(node);
    }

    public <C> Attempt<ConstructorView<? extends C>, ? extends ApplicationException> findConstructor(TypePathNode<C> node) {
        return this.findConstructor(node, true);
    }

    private <C> Attempt<ConstructorView<? extends C>, ? extends ApplicationException> findConstructor(TypePathNode<C> node, boolean checkForCycles) {
        BindingHierarchy<C> hierarchy = applicationContext.hierarchy(node.componentKey());
        Option<Provider<C>> providerOption = hierarchy.highestPriority();
        return providerOption.absent()
                ? this.findConstructor(node, node.type(), checkForCycles)
                : this.findConstructorInHierarchy(node, checkForCycles, providerOption);
    }

    private <C> Attempt<ConstructorView<? extends C>, ? extends ApplicationException> findConstructorInHierarchy(TypePathNode<C> node,
            boolean checkForCycles, Option<Provider<C>> providerOption) {
        Provider<C> provider = providerOption.get();
        if (provider instanceof ComposedProvider<C> composedProvider) {
            provider = composedProvider.provider();
        }

        if (provider instanceof TypeAwareProvider<C> typeAwareProvider) {
            TypeView<? extends C> typeView = applicationContext.environment().introspect(typeAwareProvider.type());
            return this.findConstructor(node, typeView, checkForCycles);
        }
        return Attempt.of(new NoSuchProviderException(ProviderType.TYPE_AWARE, node.componentKey()));
    }

    private <C> Attempt<ConstructorView<? extends C>, ? extends ApplicationException> findConstructor(TypePathNode<C> node, TypeView<? extends C> type, boolean checkForCycles) {
        if (type.modifiers().isAbstract()) {
            return Attempt.empty();
        }

        ConstructorView<? extends C> optimalConstructor;
        List<? extends ConstructorView<? extends C>> constructors = this.findAvailableConstructors(type);
        if (constructors.isEmpty()) {
            return Attempt.of(new MissingInjectConstructorException(type));
        }

        // An optimal constructor is the one with the highest amount of injectable parameters, so as many dependencies
        // can be satiated at once.
        optimalConstructor = constructors.get(0);
        for (ConstructorView<? extends C> constructor : constructors) {
            if (optimalConstructor.parameters().count() < constructor.parameters().count()) {
                optimalConstructor = constructor;
            }
        }

        if (checkForCycles) {
            ConstructorDiscoveryList path = this.findCyclicPath(optimalConstructor, node);
            if (!path.isEmpty()) {
                return Attempt.of(new CyclicComponentException(path));
            }
        }

        return Attempt.of(optimalConstructor);
    }

    private <C> List<ConstructorView<C>> findAvailableConstructors(TypeView<C> type) {
        List<ConstructorView<C>> constructors = type.constructors().injectable();
        if (constructors.isEmpty()) {
            Option<ConstructorView<C>> defaultConstructor = type.constructors().defaultConstructor();
            if (defaultConstructor.present()) {
                return List.of(defaultConstructor.get());
            }
            else if(type.constructors().count() == 1) {
                return List.of(type.constructors().all().get(0));
            }
        }
        return constructors;
    }

    public <T> ConstructorDiscoveryList findCyclicPath(TypeView<T> type) {
        TypePathNode<T> node = new TypePathNode<>(type, ComponentKey.of(type));
        return this.findCyclicPath(node);
    }

    public ConstructorDiscoveryList findCyclicPath(TypePathNode<?> node) {
        return this.findConstructor(node, false)
                .map(constructor -> this.findCyclicPath(constructor, node))
                .orElse(ConstructorDiscoveryList.EMPTY);
    }

    private ConstructorDiscoveryList findCyclicPath(ConstructorView<?> constructor, TypePathNode<?> node) {
        ConstructorDiscoveryList discoveryList = new ConstructorDiscoveryList();
        discoveryList.add(node, constructor);
        return this.findCyclicPath(constructor, discoveryList);
    }

    private ConstructorDiscoveryList findCyclicPath(ConstructorView<?> constructor, ConstructorDiscoveryList discoveryList) {
        if (constructor.parameters().count() == 0) {
            return ConstructorDiscoveryList.EMPTY;
        }
        ConstructorDiscoveryList parameterDiscoveryList = this.visitParameters(constructor, discoveryList);
        return Objects.requireNonNullElse(parameterDiscoveryList, ConstructorDiscoveryList.EMPTY);
    }

    @Nullable
    private ConstructorDiscoveryList visitParameters(ConstructorView<?> constructor, ConstructorDiscoveryList discoveryList) {
        for (ParameterView<?> parameterType : constructor.parameters().all()) {
            TypePathNode<?> pathNode = this.createPathNode(parameterType);
            if (discoveryList.contains(pathNode)) {
                return discoveryList;
            }

            ConstructorDiscoveryList candidateCyclicDiscoveryList = this.getDiscoveryList(discoveryList, pathNode);
            if(candidateCyclicDiscoveryList != null) {
                return candidateCyclicDiscoveryList;
            }
        }
        return null;
    }

    @Nullable
    private ConstructorDiscoveryList getDiscoveryList(ConstructorDiscoveryList discoveryList, TypePathNode<?> pathNode) {
        Option<? extends ConstructorView<?>> parameterConstructorOption = this.findConstructor(pathNode, false);
        if (parameterConstructorOption.present()) {
            ConstructorView<?> parameterConstructor = parameterConstructorOption.get();
            discoveryList.add(pathNode, parameterConstructor);

            ConstructorDiscoveryList candidateCyclicDiscoveryList = this.findCyclicPath(parameterConstructor, discoveryList);
            if (!candidateCyclicDiscoveryList.isEmpty()) {
                return candidateCyclicDiscoveryList;
            }
        }
        return null;
    }

    private <T> TypePathNode<?> createPathNode(ParameterView<T> parameter) {
        TypeView<T> type = parameter.genericType();
        ComponentKey<T> componentKey = ComponentKey.of(parameter);
        return new TypePathNode<>(type, componentKey);
    }
}
