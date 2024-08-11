package org.dockbox.hartshorn.properties.loader.path;

public sealed interface PropertyPathNode permits PropertyFieldPathNode, PropertyIndexPathNode, PropertyRootPathNode {

    String name();

    PropertyPathNode parent();

    default PropertyPathNode property(String name) {
        return new PropertyFieldPathNode(name, this);
    }

    default PropertyPathNode index(int index) {
        return new PropertyIndexPathNode(index, this);
    }
}
