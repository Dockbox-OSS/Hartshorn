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

package org.dockbox.hartshorn.inject.processing;

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.processing.Binds;
import org.dockbox.hartshorn.util.introspect.view.AnnotatedElementView;

import java.util.HashSet;
import java.util.Set;

public class ProviderContext {

    private final Set<ComponentKey<?>> dependencies = new HashSet<>();

    private final ComponentKey<?> key;
    private final AnnotatedElementView element;
    private final Binds binding;

    public ProviderContext(final ComponentKey<?> key, final AnnotatedElementView element, final Binds binding) {
        this.key = key;
        this.element = element;
        this.binding = binding;
    }

    public void dependency(final ComponentKey<?> key) {
        this.dependencies.add(key);
    }

    public void dependencies(final Set<ComponentKey<?>> keys) {
        this.dependencies.addAll(keys);
    }

    public Set<ComponentKey<?>> dependencies() {
        return this.dependencies;
    }

    public ComponentKey<?> key() {
        return this.key;
    }

    public AnnotatedElementView element() {
        return this.element;
    }

    public Binds provider() {
        return this.binding;
    }
}
