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

package org.dockbox.hartshorn.component.processing;

import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.inject.binding.collection.ComponentCollection;

/**
 * The type of binding provider. This is used to indicate whether the binding provider is a standalone component,
 * or a part of a collection.
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public enum ComponentMemberType {

    /**
     * Represents a standalone component, meaning the component will be part of a direct {@link BindingHierarchy}.
     */
    STANDALONE,

    /**
     * Represents a component that is part of a collection, meaning the component will be part of a {@link ComponentCollection}.
     * Collection components are typically used to provide multiple implementations of a single interface.
     */
    COMPOSITE,
}
