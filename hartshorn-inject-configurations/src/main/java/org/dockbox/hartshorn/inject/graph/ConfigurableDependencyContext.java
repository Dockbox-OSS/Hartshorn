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
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.inject.IllegalScopeException;
import org.dockbox.hartshorn.inject.provider.PrototypeInstantiationStrategy;
import org.dockbox.hartshorn.inject.binding.BindingFunction;
import org.dockbox.hartshorn.inject.graph.declaration.AbstractDependencyContext;
import org.dockbox.hartshorn.inject.graph.declaration.DependencyContext;
import org.dockbox.hartshorn.inject.graph.declaration.LifecycleAwareDependencyContext;
import org.dockbox.hartshorn.inject.scope.ScopeKey;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.describe.ObjectDescriber;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.View;

/**
 * A {@link DependencyContext} implementation that is used for auto-configuring components.
 * Auto-configuring components are components that are created by the container, based on a
 * {@link BindingFunction} that is registered with the container.
 *
 * <p>Typically, this represents a {@link MethodView} that is invoked when the component is
 * requested from the container.
 *
 * @param <T> the type of the component that is auto-configured
 *
 * @author Guus Lieben
 * @see DependencyContext
 * @see BindingFunction
 * @see MethodView
 * @since 0.5.0
 */
public class ConfigurableDependencyContext<T> extends AbstractDependencyContext<T>
    implements LifecycleAwareDependencyContext<T> {

    private final PrototypeInstantiationStrategy<T> supplier;
    private final View view;

    protected ConfigurableDependencyContext(AutoConfiguringDependencyContextBuilder<T> builder) {
        super(builder);
        this.supplier = builder.supplier;
        this.view = builder.view;
    }

    /**
     * Creates a new builder for a {@link ConfigurableDependencyContext}.
     *
     * @param componentKey the component key of the component being auto-configured
     * @param <T> the type of the component being auto-configured
     *
     * @return the builder for the configurable dependency context
     */
    public static <T> AutoConfiguringDependencyContextBuilder<T> builder(ComponentKey<T> componentKey) {
        return new AutoConfiguringDependencyContextBuilder<>(componentKey);
    }

    @Override
    public void configure(BindingFunction<T> function) throws ComponentConfigurationException {
        function.priority(this.priority());
        try {
            ScopeKey scope = this.scope().orNull();
            if (scope != null) {
                function.installTo(scope);
            }
        }
        catch (IllegalScopeException e) {
            throw new ComponentConfigurationException("Could not configure binding for %s".formatted(
                this.componentKey()), e);
        }
        function.priority(this.priority());
        function.processAfterInitialization(this.processAfterInitialization());

        switch (this.memberType()) {
            case STANDALONE -> this.configureComponent(function);
            case COMPOSITE -> this.configureCollection(function);
        }
    }

    private void configureCollection(BindingFunction<T> function)
        throws ComponentConfigurationException {
        function.collect(collector -> {
            InstanceType instanceType = this.instanceType();
            try {
                switch (instanceType) {
                    case SUPPLIER -> collector.supplier(this.supplier);
                    case SINGLETON ->
                        collector.singleton(this.supplier.get(ComponentRequestContext.createForComponent(),
                            null));
                    case LAZY_SINGLETON -> collector.lazySingleton(scope -> this.supplier.get(
                        ComponentRequestContext.createForComponent(),
                        scope));
                }
            }
            catch (ApplicationException e) {
                throw new ComponentConfigurationException("Could not configure binding for %s".formatted(
                    this.componentKey()), e);
            }
        });
    }

    private void configureComponent(BindingFunction<T> function)
        throws ComponentConfigurationException {
        InstanceType instanceType = this.instanceType();
        try {
            switch (instanceType) {
                case SUPPLIER -> function.to(this.supplier);
                case SINGLETON ->
                    function.singleton(this.supplier.get(ComponentRequestContext.createForComponent(),
                        null));
                case LAZY_SINGLETON -> function.lazySingleton(scope -> this.supplier.get(
                    ComponentRequestContext.createForComponent(),
                    scope));
            }
        }
        catch (ApplicationException e) {
            throw new ComponentConfigurationException("Could not configure binding for %s".formatted(
                this.componentKey()), e);
        }
    }

    @Override
    public View origin() {
        return this.view;
    }

    @Override
    public String describe() {
        return this.view.qualifiedName();
    }

    private InstanceType instanceType() {
        return switch (this.lifecycleType()) {
            case PROTOTYPE -> InstanceType.SUPPLIER;
            case SINGLETON -> {
                // Scopes are always lazy, as scopes are not guaranteed to be available at configuration time
                if (this.lazy() || this.scope().present()) {
                    yield InstanceType.LAZY_SINGLETON;
                }
                else {
                    yield InstanceType.SINGLETON;
                }
            }
        };
    }

    @Override
    protected void customizeDescriber(ObjectDescriber<?> describer) {
        super.customizeDescriber(describer);
        describer.field("view", this.view.qualifiedName());
    }

    /**
     * The type of instance that is created by the container.
     */
    private enum InstanceType {
        SUPPLIER,
        SINGLETON,
        LAZY_SINGLETON
    }

    /**
     * A builder for {@link ConfigurableDependencyContext} instances.
     *
     * @param <T> the type of the component that is auto-configured
     *
     * @author Guus Lieben
     * @since 0.5.0
     */
    public static class AutoConfiguringDependencyContextBuilder<T>
        extends AbstractDependencyContextBuilder<T, AutoConfiguringDependencyContextBuilder<T>> {

        private PrototypeInstantiationStrategy<T> supplier;
        private View view;

        protected AutoConfiguringDependencyContextBuilder(ComponentKey<T> componentKey) {
            super(componentKey);
        }

        @Override
        protected AutoConfiguringDependencyContextBuilder<T> self() {
            return this;
        }

        /**
         * The supplier that creates instances of the component.
         *
         * @param supplier the supplier
         *
         * @return the builder
         */
        public AutoConfiguringDependencyContextBuilder<T> supplier(PrototypeInstantiationStrategy<T> supplier) {
            this.supplier = supplier;
            return this;
        }

        /**
         * The view that is the origin of this dependency context.
         *
         * @param view the view
         *
         * @return the builder
         */
        public AutoConfiguringDependencyContextBuilder<T> view(View view) {
            this.view = view;
            return this;
        }

        @Override
        public ConfigurableDependencyContext<T> build() {
            return new ConfigurableDependencyContext<>(this);
        }
    }
}
