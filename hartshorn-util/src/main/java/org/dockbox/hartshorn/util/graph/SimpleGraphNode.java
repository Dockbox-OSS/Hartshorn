package org.dockbox.hartshorn.util.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SimpleGraphNode<T> implements GraphNode<T> {

    private final T value;
    private final Set<GraphNode<T>> parents = new HashSet<>();
    private final Set<GraphNode<T>> children = new HashSet<>();

    public SimpleGraphNode(final T value) {
        this.value = value;
    }

    @Override
    public T value() {
        return this.value;
    }

    @Override
    public Set<GraphNode<T>> parents() {
        return Set.copyOf(this.parents);
    }

    @Override
    public Set<GraphNode<T>> children() {
        return Set.copyOf(this.children);
    }

    @Override
    public void addParent(final GraphNode<T> parent) {
        if (!this.parents.contains(parent)) {
            this.parents.add(parent);
            parent.addChild(this);
        }
    }

    @Override
    public void addParents(final Collection<GraphNode<T>> parents) {
        parents.forEach(this::addParent);
    }

    @Override
    public void addChild(final GraphNode<T> child) {
        if (!this.children.contains(child)) {
            this.children.add(child);
            child.addParent(this);
        }
    }

    @Override
    public void addChildren(final Collection<GraphNode<T>> children) {
        children.forEach(this::addChild);
    }

    @Override
    public String toString() {
        return "SimpleGraphNode{" +
                "value=" + this.value +
                '}';
    }
}
