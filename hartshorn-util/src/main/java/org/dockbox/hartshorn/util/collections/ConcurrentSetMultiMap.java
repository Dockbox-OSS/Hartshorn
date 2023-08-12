package org.dockbox.hartshorn.util.collections;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentSetMultiMap<K, V> extends ConcurrentMultiMap<K, V> {

    public ConcurrentSetMultiMap() {
    }

    public ConcurrentSetMultiMap(final MultiMap<K, V> map) {
        super(map);
    }

    @Override
    protected Collection<V> createEmptyCollection() {
        return ConcurrentHashMap.newKeySet();
    }
}
