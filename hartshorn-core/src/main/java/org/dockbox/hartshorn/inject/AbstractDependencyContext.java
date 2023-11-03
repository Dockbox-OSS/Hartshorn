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

import java.util.Set;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.Scope;
import org.dockbox.hartshorn.component.processing.Binds;
import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;

/**
 * A simple implementation of {@link DependencyContext} that is used for components that are not managed by the container,
 * and rely on metadata that is provided by the declaration of the dependency. This context is typically used for
 * components that are created by the container, but are not automatically registered as a component, e.g. {@link Binds} declarations.
 *
 * @param <T> the type of the component
 *
 * @see DependencyContext
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public abstract class AbstractDependencyContext<T> implements DependencyContext<T> {

    private final ComponentKey<T> componentKey;
    private final Class<? extends Scope> scope;
    private final DependencyMap dependencies;
    private final int priority;

    private boolean lazy;
    private boolean singleton;
    private boolean processAfterInitialization = true;

    protected AbstractDependencyContext(ComponentKey<T> componentKey, DependencyMap dependencies, Class<? extends Scope> scope, int priority) {
        this.componentKey = componentKey;
        this.dependencies = dependencies;
        this.scope = scope;
        this.priority = priority;
    }

    /**
     * Whether the component should be created lazily. If {@code true}, the component will only be created when it is
     * requested for the first time. If {@code false}, the component will be created when the container is initialized.
     *
     * <p>Only effective when the component is also a singleton.
     *
     * @param lazy whether the component should be created lazily
     * @return this context
     */
    public AbstractDependencyContext<T> lazy(boolean lazy) {
        this.lazy = lazy;
        return this;
    }

    /**
     * Whether the component should be created as a singleton. If {@code true}, the component will be created only once,
     * and the same instance will be returned for each request. If {@code false}, a new instance will be created for
     * each request.
     *
     * @param singleton whether the component should be created as a singleton
     * @return this context
     */
    public AbstractDependencyContext<T> singleton(boolean singleton) {
        this.singleton = singleton;
        return this;
    }

    /**
     * Whether the component should be processed using {@link ComponentPostProcessor}s after it has been initialized. If
     * {@code true}, the component will be processed after it has been initialized. If {@code false}, the component will
     * not be processed automatically.
     *
     * @param processAfterInitialization whether the component should be processed after it has been initialized
     * @return this context
     */
    public AbstractDependencyContext<T> processAfterInitialization(boolean processAfterInitialization) {
        this.processAfterInitialization = processAfterInitialization;
        return this;
    }

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
        return this.priority;
    }

    @Override
    public Class<? extends Scope> scope() {
        return this.scope;
    }

    /**
     * Whether the component should be created lazily. If {@code true}, the component will only be created when it is
     * requested for the first time. If {@code false}, the component will be created when the container is initialized.
     *
     * <p>Only effective when the component is also a singleton.
     *
     * @return whether the component should be created lazily
     */
    public boolean lazy() {
        return this.lazy;
    }

    /**
     * Whether the component should be created as a singleton. If {@code true}, the component will be created only once,
     * and the same instance will be returned for each request. If {@code false}, a new instance will be created for
     * each request.
     *
     * @return whether the component should be created as a singleton
     */
    public boolean singleton() {
        return this.singleton;
    }

    /**
     * Whether the component should be processed using {@link ComponentPostProcessor}s after it has been initialized. If
     * {@code true}, the component will be processed after it has been initialized. If {@code false}, the component will
     * not be processed automatically.
     *
     * @return whether the component should be processed after it has been initialized
     */
    public boolean processAfterInitialization() {
        return this.processAfterInitialization;
    }
}
