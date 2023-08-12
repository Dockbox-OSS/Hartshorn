package org.dockbox.hartshorn.util.collections;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentSetTreeMultiMap<K extends Comparable<K>, V> extends TreeMultiMap<K, V> {

    public ConcurrentSetTreeMultiMap() {
    }

    public ConcurrentSetTreeMultiMap(final MultiMap<K, V> map) {
        super(map);
    }

    @Override
    protected Collection<V> createEmptyCollection() {
        return ConcurrentHashMap.newKeySet();
    }
}
