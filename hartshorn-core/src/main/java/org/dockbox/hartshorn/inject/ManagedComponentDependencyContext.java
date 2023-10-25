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

import java.util.Set;

public class ManagedComponentDependencyContext<T> implements DependencyContext<T> {

    private final ComponentKey<T> componentKey;
    private final Set<ComponentKey<?>> dependencies;

    public ManagedComponentDependencyContext(ComponentKey<T> componentKey, Set<ComponentKey<?>> dependencies) {
        this.componentKey = componentKey;
        this.dependencies = dependencies;
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
        return -1;
    }

    @Override
    public Class<? extends Scope> scope() {
        return Scope.DEFAULT_SCOPE.installableScopeType();
    }

    @Override
    public void configure(BindingFunction<T> function) throws ComponentConfigurationException {
        // Do nothing, require processing or standard instance provision
    }
}
