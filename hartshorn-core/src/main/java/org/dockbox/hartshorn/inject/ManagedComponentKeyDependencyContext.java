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

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

/**
 * TODO: #1060 Add documentation
 *
 * @param <T> ...
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public class ManagedComponentKeyDependencyContext<T> extends ManagedComponentDependencyContext<T> {

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

    public static <T> ManagedComponentKeyDependencyContextBuilder<T> builder(ComponentKey<T> componentKey, TypeView<T> type) {
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

    /**
     * TODO: #1060 Add documentation
     *
     * @param <T> ...
     *
     * @since 0.6.0
     *
     * @author Guus Lieben
     */
    public static final class ManagedComponentKeyDependencyContextBuilder<T> {

        private final TypeView<T> type;
        private final ComponentKey<T> componentKey;

        private boolean processAfterInitialization;
        private LifecycleType lifecycleType;
        private boolean lazy;
        private DependencyMap dependencies;
        private ConstructorView<? extends T> constructorView;

        private ManagedComponentKeyDependencyContextBuilder(ComponentKey<T> componentKey, TypeView<T> type) {
            this.componentKey = componentKey;
            this.type = type;
        }

        public ManagedComponentKeyDependencyContextBuilder<T> processAfterInitialization(boolean processAfterInitialization) {
            this.processAfterInitialization = processAfterInitialization;
            return this;
        }

        public ManagedComponentKeyDependencyContextBuilder<T> lifecycleType(LifecycleType lifecycleType) {
            this.lifecycleType = lifecycleType;
            return this;
        }

        public ManagedComponentKeyDependencyContextBuilder<T> lazy(boolean lazy) {
            this.lazy = lazy;
            return this;
        }

        public ManagedComponentKeyDependencyContextBuilder<T> dependencies(DependencyMap dependencies) {
            this.dependencies = dependencies;
            return this;
        }

        public ManagedComponentKeyDependencyContextBuilder<T> constructorView(ConstructorView<? extends T> constructorView) {
            this.constructorView = constructorView;
            return this;
        }

        public ManagedComponentKeyDependencyContext<T> build() {
            return new ManagedComponentKeyDependencyContext<>(this);
        }
    }
}
