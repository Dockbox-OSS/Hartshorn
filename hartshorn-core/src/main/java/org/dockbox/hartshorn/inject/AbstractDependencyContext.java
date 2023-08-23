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

import java.util.Set;

public abstract class AbstractDependencyContext<T> implements DependencyContext<T> {

    private final ComponentKey<T> componentKey;
    private final Set<ComponentKey<?>> dependencies;
    private final Class<? extends Scope> scope;
    private final int priority;

    private boolean lazy;
    private boolean singleton;

    protected AbstractDependencyContext(final ComponentKey<T> componentKey, final Set<ComponentKey<?>> dependencies,
                                            final Class<? extends Scope> scope, final int priority) {
        this.componentKey = componentKey;
        this.dependencies = Set.copyOf(dependencies);
        this.scope = scope;
        this.priority = priority;
    }

    public AbstractDependencyContext<T> lazy(final boolean lazy) {
        this.lazy = lazy;
        return this;
    }

    public AbstractDependencyContext<T> singleton(final boolean singleton) {
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

    public boolean lazy() {
        return lazy;
    }

    public boolean singleton() {
        return singleton;
    }
}
