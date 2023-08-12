package org.dockbox.hartshorn.util.collections;

import java.util.Collection;
import java.util.HashSet;

public class SynchronizedHashSetMultiMap<K, V> extends SynchronizedMultiMap<K, V> {

    public SynchronizedHashSetMultiMap() {
    }

    public SynchronizedHashSetMultiMap(final MultiMap<K, V> map) {
        super(map);
    }

    @Override
    protected Collection<V> createEmptyCollection() {
        return new HashSet<>();
    }
}
