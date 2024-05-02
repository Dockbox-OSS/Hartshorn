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

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.ScopeKey;
import org.dockbox.hartshorn.component.processing.Binds;
import org.dockbox.hartshorn.component.processing.ComponentMemberType;
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
    private final DependencyMap dependencies;
    private final ScopeKey scope;
    private final int priority;
    private final ComponentMemberType memberType;

    private boolean lazy;
    private LifecycleType lifecycleType;
    private boolean processAfterInitialization = true;

    protected AbstractDependencyContext(AbstractDependencyContextBuilder<T, ?> builder) {
        this.componentKey = builder.componentKey;
        this.dependencies = builder.dependencies;
        this.scope = builder.scope;
        this.priority = builder.priority;
        this.memberType = builder.memberType;
        this.lazy = builder.lazy;
        this.lifecycleType = builder.lifecycleType;
        this.processAfterInitialization = builder.processAfterInitialization;
    }

    protected AbstractDependencyContext(DependencyContext<T> dependencyContext) {
        this(
            dependencyContext.componentKey(),
            dependencyContext.dependencies(),
            dependencyContext.scope(),
            dependencyContext.priority(),
            dependencyContext.memberType()
        );

        if (dependencyContext instanceof AbstractDependencyContext<T> abstractDependencyContext) {
            this.lazy = abstractDependencyContext.lazy();
            this.lifecycleType = abstractDependencyContext.lifecycleType();
            this.processAfterInitialization = abstractDependencyContext.processAfterInitialization();
        }
        else {
            this.lazy = false;
            this.lifecycleType = LifecycleType.PROTOTYPE; // TODO: Dynamic?
            this.processAfterInitialization = true;
        }
    }

    protected AbstractDependencyContext(ComponentKey<T> componentKey, DependencyMap dependencies,
                                        ScopeKey scope, int priority, ComponentMemberType memberType) {
        this.componentKey = componentKey;
        this.dependencies = dependencies;
        this.scope = scope;
        this.priority = priority;
        this.memberType = memberType;
    }

    /**
     * Whether the component should be created lazily. If {@code true}, the component will only be created when it is
     * requested for the first time. If {@code false}, the component will be created when the container is initialized.
     *
     * <p>Only effective when the component's {@link #lifecycleType()} is {@link LifecycleType#SINGLETON 'Singleton'}.
     *
     * @param lazy whether the component should be created lazily
     * @return this context
     */
    public AbstractDependencyContext<T> lazy(boolean lazy) {
        this.lazy = lazy;
        return this;
    }

    public AbstractDependencyContext<T> lifecycleType(LifecycleType lifecycleType) {
        this.lifecycleType = lifecycleType;
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
    public ScopeKey scope() {
        return this.scope;
    }

    @Override
    public ComponentMemberType memberType() {
        return this.memberType;
    }

    /**
     * Whether the component should be created lazily. If {@code true}, the component will only be created when it is
     * requested for the first time. If {@code false}, the component will be created when the container is initialized.
     *
     * <p>Only effective when the component's {@link #lifecycleType()} is {@link LifecycleType#SINGLETON 'Singleton'}.
     *
     * @return whether the component should be created lazily
     */
    @Override
    public boolean lazy() {
        return this.lazy;
    }

    /**
     * TODO: Document
     * @return
     */
    @Override
    public LifecycleType lifecycleType() {
        return this.lifecycleType;
    }

    /**
     * Whether the component should be processed using {@link ComponentPostProcessor}s after it has been initialized. If
     * {@code true}, the component will be processed after it has been initialized. If {@code false}, the component will
     * not be processed automatically.
     *
     * @return whether the component should be processed after it has been initialized
     */
    @Override
    public boolean processAfterInitialization() {
        return this.processAfterInitialization;
    }

    public abstract static class AbstractDependencyContextBuilder<T, B extends AbstractDependencyContextBuilder<T, B>> {

        private final ComponentKey<T> componentKey;

        private DependencyMap dependencies;
        private ScopeKey scope;
        private int priority;
        private ComponentMemberType memberType;
        private boolean lazy;
        private LifecycleType lifecycleType;
        private boolean processAfterInitialization;

        protected AbstractDependencyContextBuilder(ComponentKey<T> componentKey) {
            this.componentKey = componentKey;
        }

        public B dependencies(DependencyMap dependencies) {
            this.dependencies = dependencies;
            return this.self();
        }

        public B scope(ScopeKey scope) {
            this.scope = scope;
            return this.self();
        }

        public B priority(int priority) {
            this.priority = priority;
            return this.self();
        }

        public B memberType(ComponentMemberType memberType) {
            this.memberType = memberType;
            return this.self();
        }

        public B lazy(boolean lazy) {
            this.lazy = lazy;
            return this.self();
        }

        public B lifecycleType(LifecycleType lifecycleType) {
            this.lifecycleType = lifecycleType;
            return this.self();
        }

        public B processAfterInitialization(boolean processAfterInitialization) {
            this.processAfterInitialization = processAfterInitialization;
            return this.self();
        }

        protected abstract B self();

        public abstract DependencyContext<T> build();
    }
}
