package org.dockbox.hartshorn.util.collections;

import java.util.ArrayList;
import java.util.Collection;

public class SynchronizedArrayListMultiMap<K, V> extends SynchronizedMultiMap<K, V> {

    public SynchronizedArrayListMultiMap() {
    }

    public SynchronizedArrayListMultiMap(final MultiMap<K, V> map) {
        super(map);
    }

    @Override
    protected Collection<V> createEmptyCollection() {
        return new ArrayList<>();
    }
}
