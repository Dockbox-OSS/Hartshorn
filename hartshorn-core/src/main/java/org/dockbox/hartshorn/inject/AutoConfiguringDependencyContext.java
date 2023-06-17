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

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.Scope;
import org.dockbox.hartshorn.inject.binding.BindingFunction;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.function.CheckedSupplier;

import java.util.Set;

public class AutoConfiguringDependencyContext<T> implements DependencyContext<T> {

    private final ComponentKey<T> componentKey;
    private final Set<ComponentKey<?>> dependencies;
    private final Class<? extends Scope> scope;
    private final CheckedSupplier<T> supplier;
    private final int priority;

    private boolean lazy;
    private boolean singleton;

    public AutoConfiguringDependencyContext(final ComponentKey<T> componentKey, final Set<ComponentKey<?>> dependencies,
                                            final Class<? extends Scope> scope, final int priority,
                                            final CheckedSupplier<T> supplier) {
        this.componentKey = componentKey;
        this.dependencies = Set.copyOf(dependencies);
        this.scope = scope;
        this.priority = priority;
        this.supplier = supplier;
    }

    public AutoConfiguringDependencyContext<T> lazy(final boolean lazy) {
        this.lazy = lazy;
        return this;
    }

    public AutoConfiguringDependencyContext<T> singleton(final boolean singleton) {
        this.singleton = singleton;
        return this;
    }

    @Override
    public ComponentKey<T> componentKey() {
        return this.componentKey;
    }

    @Override
    public Set<ComponentKey<?>> dependencies() {
        return this.dependencies;
    }

    @Override
    public int priority() {
        return this.priority;
    }

    @Override
    public Class<? extends Scope> scope() {
        return this.scope;
    }

    @Override
    public void configure(final BindingFunction<T> function) throws ComponentConfigurationException {
        final InstanceType instanceType = this.instanceType();
        function.priority(this.priority);
        if (this.scope != Scope.DEFAULT_SCOPE.installableScopeType()) function.installTo(this.scope);

        try {
            switch (instanceType) {
                case SUPPLIER -> function.to(this.supplier);
                case SINGLETON -> function.singleton(this.supplier.get());
                case LAZY_SINGLETON -> function.lazySingleton(this.supplier);
            }
        }
        catch (final ApplicationException e) {
            throw new ComponentConfigurationException("Could not configure binding for %s".formatted(this.componentKey), e);
        }
    }

    private InstanceType instanceType() {
        if (this.singleton && this.lazy) return InstanceType.LAZY_SINGLETON;
        else if (this.singleton) return InstanceType.SINGLETON;
        else return InstanceType.SUPPLIER;
    }

    enum InstanceType { SUPPLIER, SINGLETON, LAZY_SINGLETON }
}
