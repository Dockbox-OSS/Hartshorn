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

package org.dockbox.hartshorn.inject.strategy;

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.ScopeKey;
import org.dockbox.hartshorn.component.processing.Binds.BindingType;
import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.inject.DependencyMap;

public class BindingContext<T> extends DefaultContext {

    private final ComponentKey<T> componentKey;
    private final DependencyMap dependencies;
    private final ScopeKey scope;
    private final int priority;
    private final boolean lazy;
    private final boolean singleton;
    private final boolean processAfterInitialization;
    private final BindingType bindingType;

    public BindingContext(
            ComponentKey<T> componentKey,
            DependencyMap dependencies,
            ScopeKey scope,
            int priority,
            boolean lazy,
            boolean singleton,
            boolean processAfterInitialization,
            BindingType bindingType
    ) {
        this.componentKey = componentKey;
        this.dependencies = dependencies;
        this.scope = scope;
        this.priority = priority;
        this.lazy = lazy;
        this.singleton = singleton;
        this.processAfterInitialization = processAfterInitialization;
        this.bindingType = bindingType;
    }

    public ComponentKey<T> componentKey() {
        return componentKey;
    }

    public DependencyMap dependencies() {
        return dependencies;
    }

    public ScopeKey scope() {
        return scope;
    }

    public int priority() {
        return priority;
    }

    public boolean lazy() {
        return lazy;
    }

    public boolean singleton() {
        return singleton;
    }

    public boolean processAfterInitialization() {
        return processAfterInitialization;
    }

    public BindingType bindingType() {
        return bindingType;
    }
}
