package org.dockbox.hartshorn.util.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ArrayListHashBiMultiMap<K, V> extends AbstractBiMultiMap<K, V> {

    public ArrayListHashBiMultiMap() {
    }

    public ArrayListHashBiMultiMap(final MultiMap<K, V> map) {
        super(map);
    }

    @Override
    protected MultiMap<V, K> createEmptyInverseMap() {
        return new ArrayListMultiMap<>();
    }

    @Override
    protected Collection<V> createEmptyCollection() {
        return new ArrayList<>();
    }

    @Override
    protected Map<K, Collection<V>> createEmptyMap() {
        return new HashMap<>();
    }
}
