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

import java.util.Set;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.inject.component.ComponentRegistry;
import org.dockbox.hartshorn.component.ScopeKey;
import org.dockbox.hartshorn.inject.graph.ComponentConfigurationException;
import org.dockbox.hartshorn.inject.graph.ComponentMemberType;
import org.dockbox.hartshorn.inject.binding.BindingFunction;
import org.dockbox.hartshorn.inject.graph.declaration.DependencyContext;
import org.dockbox.hartshorn.inject.graph.DependencyMap;
import org.dockbox.hartshorn.inject.graph.DependencyResolutionType;
import org.dockbox.hartshorn.inject.graph.declaration.LifecycleAwareDependencyContext;
import org.dockbox.hartshorn.inject.provider.LifecycleType;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.introspect.view.View;

/**
 * A {@link DependencyContext} implementation that is used for managed components. Managed components are components that
 * are managed by the container. Typically, these are obtained through the active {@link ComponentRegistry}.
 *
 * @param <T> the type of the component that is managed
 *
 * @see DependencyContext
 * @see ComponentRegistry
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public abstract class ManagedComponentDependencyContext<T> implements LifecycleAwareDependencyContext<T> {

    private final ComponentKey<T> componentKey;
    private final DependencyMap dependencies;
    private final ConstructorView<? extends T> constructorView;

    public ManagedComponentDependencyContext(ComponentKey<T> componentKey, DependencyMap dependencies, ConstructorView<? extends T> constructorView) {
        this.componentKey = componentKey;
        this.dependencies = dependencies;
        this.constructorView = constructorView;
    }

    protected abstract TypeView<T> type();

    @Override
    public ComponentKey<T> componentKey() {
        return this.componentKey;
    }

    @Override
    public DependencyMap dependencies() {
        return this.dependencies;
    }

    @Override
    public Set<ComponentKey<?>> dependencies(DependencyResolutionType resolutionType) {
        return Set.copyOf(this.dependencies.get(resolutionType));
    }

    @Override
    public boolean needsImmediateResolution(ComponentKey<?> dependencyCandidate) {
        return this.dependencies(DependencyResolutionType.IMMEDIATE).contains(dependencyCandidate);
    }

    @Override
    public int priority() {
        return -1;
    }

    @Override
    public ScopeKey scope() {
        return ApplicationContext.APPLICATION_SCOPE;
    }

    @Override
    public ComponentMemberType memberType() {
        return ComponentMemberType.STANDALONE;
    }

    @Override
    public View origin() {
        return this.constructorView;
    }

    @Override
    public void configure(BindingFunction<T> function) throws ComponentConfigurationException {
        Class<T> componentType = this.type().type();
        LifecycleType lifecycleType = this.lifecycleType();
        switch (lifecycleType) {
            // At this point we ignore the ComponentContainer#lazy() property. This will later be handled
            // by the context constructor when the application is ready for initialization.
            case SINGLETON -> function.lazySingleton(componentType);
            case PROTOTYPE -> function.to(componentType);
            default -> throw new ComponentConfigurationException("Unsupported lifecycle: " + lifecycleType);
        }
    }
}
