package org.dockbox.hartshorn.util.collections;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public abstract class TreeMultiMap<K extends Comparable<K>, V> extends StandardMultiMap<K, V> {

    protected TreeMultiMap() {
    }

    protected TreeMultiMap(final MultiMap<K, V> map) {
        super(map);
    }

    @Override
    protected Map<K, Collection<V>> createEmptyMap() {
        return new TreeMap<>();
    }
}
