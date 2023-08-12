package org.dockbox.hartshorn.util.collections;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

public class CopyOnWriteArrayListMultiMap<K, V> extends ConcurrentMultiMap<K, V> {

    public CopyOnWriteArrayListMultiMap() {
    }

    public CopyOnWriteArrayListMultiMap(final MultiMap<K, V> map) {
        super(map);
    }

    @Override
    protected Collection<V> createEmptyCollection() {
        return new CopyOnWriteArrayList<>();
    }
}
