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

package org.dockbox.hartshorn.inject.graph.support;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.inject.graph.TypePathNode;
import org.dockbox.hartshorn.inject.graph.support.ComponentDiscoveryList.DiscoveredComponent;
import org.dockbox.hartshorn.util.CollectionUtilities;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A list of components that were discovered during the validation of potentially cyclic components. This list is
 * used to determine the origin of a cycle dependency, and to provide a list of all components that are involved
 * in the cycle.
 *
 * @see CyclicDependencyGraphValidator
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ComponentDiscoveryList implements Iterable<DiscoveredComponent> {

    private final List<DiscoveredComponent> discoveredComponents;

    public ComponentDiscoveryList() {
        this(new LinkedList<>());
    }

    public ComponentDiscoveryList(List<DiscoveredComponent> discoveredComponents) {
        this.discoveredComponents = discoveredComponents;
    }

    /**
     * Adds a new component to the list. The component is added to the front of the list, as it's the most recent
     * component that was discovered.
     *
     * @param node the component to add
     */
    public void add(TypePathNode<?> node) {
        this.discoveredComponents.addFirst(new DiscoveredComponent(node, node.type()));
    }

    /**
     * Adds a new component to the list. The component is added to the front of the list, as it's the most recent
     * component that was discovered.
     *
     * @param node the component to add
     * @param actualType the actual type of the component
     */
    public void add(TypePathNode<?> node, TypeView<?> actualType) {
        this.discoveredComponents.addFirst(new DiscoveredComponent(node, actualType));
    }

    /**
     * Adds a new component to the list. The component is added to the front of the list, as it's the most recent
     * component that was discovered.
     *
     * @param node the component to add
     * @param constructor the constructor that was used to create the component
     */
    public void add(TypePathNode<?> node, ConstructorView<?> constructor) {
        this.discoveredComponents.addFirst(new DiscoveredComponent(node, constructor.declaredBy()));
    }

    /**
     * Returns a list of all discovered components. The list is ordered from most recently discovered to least
     * recently discovered. The list is also de-duplicated, as the first component may also be included at the
     * end of the list.
     *
     * @return a list of all discovered components
     */
    public List<DiscoveredComponent> discoveredComponents() {
        return CollectionUtilities.distinct(this.discoveredComponents);
    }

    /**
     * Returns a list of all discovered components. The list is ordered from most recently discovered to least
     * recently discovered. This includes all components, where the first component may also be included at the
     * end of the list.
     *
     * @return a list of all discovered components
     */
    public List<DiscoveredComponent> discoveredComponentsCyclic() {
        return List.copyOf(this.discoveredComponents);
    }

    /**
     * Returns whether the list is empty.
     *
     * @return {@code true} if the list is empty, {@code false} otherwise
     */
    public boolean isEmpty() {
        return this.discoveredComponents.isEmpty();
    }

    /**
     * Returns whether the list contains the given component.
     *
     * @param pathNode the component to check for
     * @return {@code true} if the list contains the component, {@code false} otherwise
     */
    public boolean contains(TypePathNode<?> pathNode) {
        return this.discoveredComponents.stream().anyMatch(component -> component.node().equals(pathNode));
    }

    @NonNull
    @Override
    public Iterator<DiscoveredComponent> iterator() {
        return this.discoveredComponents().iterator();
    }

    /**
     * Returns the origin of the cycle. The origin is the first component that was discovered.
     *
     * @return the origin of the cycle
     */
    public DiscoveredComponent getOrigin() {
        if (this.discoveredComponents.isEmpty()) {
            return null;
        }
        return this.discoveredComponents.getFirst();
    }

    /**
     * Represents a discovered component in the {@link ComponentDiscoveryList}. It contains the {@link TypePathNode}
     * that represents the original binding declaration, and the actual type of the component that was discovered.
     *
     * @param node the original binding declaration
     * @param actualType the actual type of the component
     *
     * @since 0.5.0
     *
     * @author Guus Lieben
     */
    public record DiscoveredComponent(TypePathNode<?> node, TypeView<?> actualType) {

        /**
         * Returns whether the component was discovered from a binding declaration, or from a constructor.
         * If the component was discovered from a binding declaration, the actual type of the component
         * will be different from the type of the dependency declaration.
         *
         * @return {@code true} if the component was discovered from a binding declaration, {@code false} otherwise
         */
        public boolean fromBinding() {
            return !this.node.type().is(this.actualType.type());
        }
    }
}
