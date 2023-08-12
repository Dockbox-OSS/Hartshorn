package org.dockbox.hartshorn.util.collections;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ConcurrentMultiMap<K, V> extends StandardMultiMap<K, V> {

    protected ConcurrentMultiMap() {
    }

    protected ConcurrentMultiMap(final MultiMap<K, V> map) {
        super(map);
    }

    @Override
    protected Map<K, Collection<V>> createEmptyMap() {
        return new ConcurrentHashMap<>();
    }
}
