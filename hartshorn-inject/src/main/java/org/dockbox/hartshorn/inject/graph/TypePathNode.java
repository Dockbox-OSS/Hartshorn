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

package org.dockbox.hartshorn.inject.graph;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.graph.support.ComponentDiscoveryList;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.introspect.view.View;

/**
 * Represents a node in a type path, typically a {@link ComponentDiscoveryList}. This node contains a {@link TypeView}
 * and a {@link ComponentKey} that represents the type and its key in the current container. The origin of the node
 * represents the view that this node was discovered from.
 *
 * @param type the introspectable type view
 * @param componentKey the component key of this node within the current container
 * @param origin the origin view that this node was discovered from
 * @param <T> the type of the introspectable type view
 *
 * @see ComponentDiscoveryList
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public record TypePathNode<T>(TypeView<T> type, ComponentKey<T> componentKey, View origin) {

    /**
     * Returns the qualified name of the type view. This is typically the fully qualified name of the type,
     * combined with any qualifiers that are defined on the component key.
     *
     * @return the qualified name of the type view
     */
    public String qualifiedName() {
        return this.componentKey.qualifiedName(true);
    }
}
