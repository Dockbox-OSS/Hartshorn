package org.dockbox.hartshorn.properties.loader.path;

public record PropertyIndexPathNode(int index, PropertyPathNode parent) implements PropertyPathNode {

    @Override
    public String name() {
        return String.valueOf(this.index);
    }
}
