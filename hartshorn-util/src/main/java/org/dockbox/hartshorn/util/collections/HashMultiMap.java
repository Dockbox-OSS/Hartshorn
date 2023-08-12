package org.dockbox.hartshorn.util.collections;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class HashMultiMap<K, V> extends StandardMultiMap<K, V> {

    protected HashMultiMap() {
    }

    protected HashMultiMap(final MultiMap<K, V> map) {
        super(map);
    }

    @Override
    protected Map<K, Collection<V>> createEmptyMap() {
        return new HashMap<>();
    }
}
