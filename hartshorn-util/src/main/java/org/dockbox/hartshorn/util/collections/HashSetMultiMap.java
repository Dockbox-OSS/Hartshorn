package org.dockbox.hartshorn.util.collections;

import java.util.Collection;
import java.util.HashSet;

public class HashSetMultiMap<K, V> extends HashMultiMap<K, V> {

    public HashSetMultiMap() {
    }

    public HashSetMultiMap(final MultiMap<K, V> map) {
        super(map);
    }

    @Override
    protected Collection<V> createEmptyCollection() {
        return new HashSet<>();
    }
}
