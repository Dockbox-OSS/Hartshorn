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

package org.dockbox.hartshorn.inject.graph;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.component.ComponentContainer;
import org.dockbox.hartshorn.inject.provider.LifecycleType;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

/**
 * A {@link ManagedComponentDependencyContext} for components that do not have associated
 * {@link ComponentContainer containers}, but are still managed by the framework.
 *
 * @param <T> the type of the component
 *
 * @author Guus Lieben
 * @since 0.6.0
 */
public final class ManagedComponentKeyDependencyContext<T>
    extends ManagedComponentDependencyContext<T> {

    private final TypeView<T> type;
    private final boolean processAfterInitialization;
    private final LifecycleType lifecycleType;
    private final boolean lazy;

    private ManagedComponentKeyDependencyContext(ManagedComponentKeyDependencyContextBuilder<T> builder) {
        super(builder.componentKey, builder.dependencies, builder.constructorView);
        this.type = builder.type;
        this.processAfterInitialization = builder.processAfterInitialization;
        this.lifecycleType = builder.lifecycleType;
        this.lazy = builder.lazy;
    }

    /**
     * Creates a new builder for a {@link ManagedComponentKeyDependencyContext}.
     *
     * @param componentKey the component key of the component
     * @param type the type of the component
     * @param <T> the type of the component
     *
     * @return the builder
     */
    public static <T> ManagedComponentKeyDependencyContextBuilder<T> builder(
        ComponentKey<T> componentKey,
        TypeView<T> type
    ) {
        return new ManagedComponentKeyDependencyContextBuilder<>(componentKey, type);
    }

    @Override
    protected TypeView<T> type() {
        return this.type;
    }

    @Override
    public boolean lazy() {
        return this.lazy;
    }

    @Override
    public LifecycleType lifecycleType() {
        return this.lifecycleType;
    }

    @Override
    public boolean processAfterInitialization() {
        return this.processAfterInitialization;
    }

    @Override
    public String describe() {
        return this.type.qualifiedName();
    }

    /**
     * Builder for creating a {@link ManagedComponentKeyDependencyContext}.
     *
     * @param <T> the type of the component
     *
     * @author Guus Lieben
     * @since 0.6.0
     */
    public static final class ManagedComponentKeyDependencyContextBuilder<T> {

        private final TypeView<T> type;
        private final ComponentKey<T> componentKey;

        private boolean processAfterInitialization;
        private LifecycleType lifecycleType;
        private boolean lazy;
        private DependencyMap dependencies;
        private ConstructorView<? extends T> constructorView;

        private ManagedComponentKeyDependencyContextBuilder(
            ComponentKey<T> componentKey,
            TypeView<T> type
        ) {
            this.componentKey = componentKey;
            this.type = type;
        }

        /**
         * Sets whether to process this component after initialization.
         *
         * @param processAfterInitialization whether to process after initialization
         *
         * @return the current builder instance
         */
        public ManagedComponentKeyDependencyContextBuilder<T> processAfterInitialization(boolean processAfterInitialization) {
            this.processAfterInitialization = processAfterInitialization;
            return this;
        }

        /**
         * Sets the lifecycle type for this context.
         *
         * @param lifecycleType the lifecycle type
         *
         * @return the current builder instance
         */
        public ManagedComponentKeyDependencyContextBuilder<T> lifecycleType(LifecycleType lifecycleType) {
            this.lifecycleType = lifecycleType;
            return this;
        }

        /**
         * Sets whether this component is to be lazily initialized. This has no effect if
         * {@link #lifecycleType()} is set to {@link LifecycleType#PROTOTYPE}.
         *
         * @param lazy whether the component is lazy
         *
         * @return the current builder instance
         */
        public ManagedComponentKeyDependencyContextBuilder<T> lazy(boolean lazy) {
            this.lazy = lazy;
            return this;
        }

        /**
         * Sets the dependencies for this context.
         *
         * @param dependencies the dependencies
         *
         * @return the current builder instance
         */
        public ManagedComponentKeyDependencyContextBuilder<T> dependencies(DependencyMap dependencies) {
            this.dependencies = dependencies;
            return this;
        }

        /**
         * Sets the constructor view for this context. The constructor view represents the
         * constructor used to instantiate the managed component.
         *
         * @param constructorView the constructor view
         *
         * @return the current builder instance
         */
        public ManagedComponentKeyDependencyContextBuilder<T> constructorView(ConstructorView<? extends T> constructorView) {
            this.constructorView = constructorView;
            return this;
        }

        /**
         * Builds the {@link ManagedComponentKeyDependencyContext}.
         *
         * @return the built context
         */
        public ManagedComponentKeyDependencyContext<T> build() {
            return new ManagedComponentKeyDependencyContext<>(this);
        }
    }
}
