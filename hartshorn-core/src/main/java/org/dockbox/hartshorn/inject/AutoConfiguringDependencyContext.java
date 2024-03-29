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
import org.dockbox.hartshorn.component.Scope;
import org.dockbox.hartshorn.component.ScopeKey;
import org.dockbox.hartshorn.component.processing.Binds.BindingType;
import org.dockbox.hartshorn.inject.binding.BindingFunction;
import org.dockbox.hartshorn.inject.binding.IllegalScopeException;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.View;

/**
 * A {@link DependencyContext} implementation that is used for auto-configuring components. Auto-configuring components
 * are components that are created by the container, based on a {@link BindingFunction} that is registered with the
 * container.
 *
 * <p>Typically, this represents a {@link MethodView} that is invoked when the component is requested from the container.
 *
 * @param <T> the type of the component that is auto-configured
 *
 * @see DependencyContext
 * @see BindingFunction
 * @see MethodView
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class AutoConfiguringDependencyContext<T> extends AbstractDependencyContext<T> {

    private final ContextAwareComponentSupplier<T> supplier;
    private final View view;

    public AutoConfiguringDependencyContext(ComponentKey<T> componentKey, DependencyMap dependencies,
                                            ScopeKey scope, int priority, BindingType bindingType,
                                            View view, ContextAwareComponentSupplier<T> supplier) {
        super(componentKey, dependencies, scope, priority, bindingType);
        this.supplier = supplier;
        this.view = view;
    }

    @Override
    public void configure(BindingFunction<T> function) throws ComponentConfigurationException {
        function.priority(this.priority());
        if (this.scope() != Scope.DEFAULT_SCOPE.installableScopeType()) {
            try {
                function.installTo(this.scope());
            }
            catch (IllegalScopeException e) {
                throw new ComponentConfigurationException("Could not configure binding for %s".formatted(this.componentKey()), e);
            }
        }
        function.priority(this.priority());
        function.processAfterInitialization(this.processAfterInitialization());

        switch(this.type()) {
            case STANDALONE -> this.configureComponent(function);
            case COLLECTION -> this.configureCollection(function);
        }
    }

    private void configureCollection(BindingFunction<T> function) throws ComponentConfigurationException {
        function.collect(collector -> {
            InstanceType instanceType = this.instanceType();
            try {
                switch(instanceType) {
                case SUPPLIER -> collector.supplier(this.supplier);
                case SINGLETON -> collector.singleton(this.supplier.get(ComponentRequestContext.createForComponent()));
                case LAZY_SINGLETON -> collector.lazySingleton(() -> this.supplier.get(ComponentRequestContext.createForComponent()));
                }
            }
            catch(ApplicationException e) {
                throw new ComponentConfigurationException("Could not configure binding for %s".formatted(this.componentKey()), e);
            }
        });
    }

    private void configureComponent(BindingFunction<T> function) throws ComponentConfigurationException {
        InstanceType instanceType = this.instanceType();
        try {
            switch (instanceType) {
                case SUPPLIER -> function.to(this.supplier);
                case SINGLETON -> function.singleton(this.supplier.get(ComponentRequestContext.createForComponent()));
                case LAZY_SINGLETON -> function.lazySingleton(() -> this.supplier.get(ComponentRequestContext.createForComponent()));
            }
        }
        catch (ApplicationException e) {
            throw new ComponentConfigurationException("Could not configure binding for %s".formatted(this.componentKey()), e);
        }
    }

    @Override
    public View origin() {
        return this.view;
    }

    private InstanceType instanceType() {
        if (this.singleton() && this.lazy()) {
            return InstanceType.LAZY_SINGLETON;
        }
        else if (this.singleton()) {
            return InstanceType.SINGLETON;
        }
        else {
            return InstanceType.SUPPLIER;
        }
    }

    /**
     * The type of instance that is created by the container. This is either a supplier, a singleton or a lazy singleton.
     */
    private enum InstanceType { SUPPLIER, SINGLETON, LAZY_SINGLETON }
}
