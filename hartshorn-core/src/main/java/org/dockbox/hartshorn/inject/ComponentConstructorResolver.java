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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.populate.ComponentInjectionPointsResolver;
import org.dockbox.hartshorn.inject.NoSuchProviderException.ProviderType;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.List;

public final class ComponentConstructorResolver {

    private final ApplicationContext applicationContext;

    private ComponentConstructorResolver(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public static ComponentConstructorResolver create(ApplicationContext applicationContext) {
        return new ComponentConstructorResolver(applicationContext);
    }

    public <C> Option<ConstructorView<? extends C>> findConstructor(TypeView<C> type)
            throws MissingInjectConstructorException, NoSuchProviderException {
        TypePathNode<C> node = new TypePathNode<>(type, ComponentKey.of(type), type);
        return this.findConstructor(node);
    }

    public <C> Option<ConstructorView<? extends C>> findConstructor(TypePathNode<C> node)
            throws MissingInjectConstructorException, NoSuchProviderException {
        BindingHierarchy<C> hierarchy = this.applicationContext.hierarchy(node.componentKey());
        int highestPriority = hierarchy.highestPriority();
        Option<Provider<C>> providerOption = hierarchy.get(highestPriority);
        return providerOption.absent()
            ? this.findConstructorInImplementation(node.type())
            : this.findConstructorInHierarchy(node, providerOption);
    }

    private <C> Option<ConstructorView<? extends C>> findConstructorInHierarchy(TypePathNode<C> node, Option<Provider<C>> providerOption)
            throws NoSuchProviderException, MissingInjectConstructorException {
        Provider<C> provider = providerOption.get();
        if (provider instanceof ComposedProvider<C> composedProvider) {
            provider = composedProvider.provider();
        }

        if (provider instanceof TypeAwareProvider<C> typeAwareProvider) {
            TypeView<? extends C> typeView = this.applicationContext.environment().introspector().introspect(typeAwareProvider.type());
            return this.findConstructorInImplementation(typeView);
        }
        throw new NoSuchProviderException(ProviderType.TYPE_AWARE, node.componentKey());
    }

    private <C> Option<ConstructorView<? extends C>> findConstructorInImplementation(TypeView<? extends C> type) throws MissingInjectConstructorException {
        if (type.modifiers().isAbstract()) {
            return Option.empty();
        }

        ConstructorView<? extends C> optimalConstructor;
        List<? extends ConstructorView<? extends C>> constructors = this.findAvailableConstructors(type);
        if (constructors.isEmpty()) {
            throw new MissingInjectConstructorException(type);
        }

        // An optimal constructor is the one with the highest amount of injectable parameters, so as many dependencies
        // can be satiated at once.
        optimalConstructor = constructors.get(0);
        for (ConstructorView<? extends C> constructor : constructors) {
            if (optimalConstructor.parameters().count() < constructor.parameters().count()) {
                optimalConstructor = constructor;
            }
        }

        return Option.of(optimalConstructor);
    }

    private <C> List<ConstructorView<C>> findAvailableConstructors(TypeView<C> type) {
        ComponentInjectionPointsResolver injectionPointsResolver = this.applicationContext.environment().injectionPointsResolver();
        List<ConstructorView<C>> constructors = type.constructors().all().stream()
                .filter(injectionPointsResolver::isInjectable)
                .toList();
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
}
