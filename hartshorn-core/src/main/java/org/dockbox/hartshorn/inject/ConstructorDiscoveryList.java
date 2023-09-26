package org.dockbox.hartshorn.inject;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.dockbox.hartshorn.inject.ConstructorDiscoveryList.DiscoveredComponent;
import org.dockbox.hartshorn.util.CollectionUtilities;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.jetbrains.annotations.NotNull;

public class ConstructorDiscoveryList implements Iterable<DiscoveredComponent> {

    public static final ConstructorDiscoveryList EMPTY = new ConstructorDiscoveryList(List.of());

    private final List<DiscoveredComponent> discoveredComponents;

    public ConstructorDiscoveryList() {
        this(new LinkedList<>());
    }

    public ConstructorDiscoveryList(List<DiscoveredComponent> discoveredComponents) {
        this.discoveredComponents = discoveredComponents;
    }

    public void add(TypePathNode<?> node, ConstructorView<?> constructor) {
        this.discoveredComponents.add(new DiscoveredComponent(node, constructor.declaredBy()));
    }

    public List<DiscoveredComponent> discoveredComponents() {
        return CollectionUtilities.distinct(this.discoveredComponents);
    }

    public boolean isEmpty() {
        return this.discoveredComponents.isEmpty();
    }

    public boolean contains(TypePathNode<?> pathNode) {
        return this.discoveredComponents.stream().anyMatch(component -> component.node().equals(pathNode));
    }

    @NotNull
    @Override
    public Iterator<DiscoveredComponent> iterator() {
        return this.discoveredComponents().iterator();
    }

    public DiscoveredComponent getOrigin() {
        if (this.discoveredComponents.isEmpty()) {
            return null;
        }
        return this.discoveredComponents.get(0);
    }

    public record DiscoveredComponent(TypePathNode<?> node, TypeView<?> actualType) {

        public boolean fromBinding() {
            return !node.type().is(actualType.type());
        }
    }
}
