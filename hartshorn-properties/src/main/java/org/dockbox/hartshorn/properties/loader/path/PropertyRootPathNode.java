package org.dockbox.hartshorn.properties.loader.path;

public final class PropertyRootPathNode implements PropertyPathNode {

    @Override
    public String name() {
        return "";
    }

    @Override
    public PropertyPathNode parent() {
        throw new UnsupportedOperationException("Root node has no parent");
    }
}
