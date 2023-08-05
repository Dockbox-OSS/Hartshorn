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

import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

public class ComponentContainerDependencyDeclarationContext<T> implements DependencyDeclarationContext<T> {

    private final ComponentContainer<T> container;

    public ComponentContainerDependencyDeclarationContext(final ComponentContainer<T> container) {
        this.container = container;
    }

    @Override
    public TypeView<T> type() {
        return this.container.type();
    }

    @Override
    public String id() {
        // Components have IDs, but these are not used for dependency resolution, so we can return null here
        return null;
    }
}
