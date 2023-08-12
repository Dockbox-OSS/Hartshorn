package org.dockbox.hartshorn.util.collections;

import java.util.ArrayList;
import java.util.Collection;

public class ArrayListMultiMap<K, V> extends HashMultiMap<K, V> {

    public ArrayListMultiMap() {
    }

    public ArrayListMultiMap(final MultiMap<K, V> map) {
        super(map);
    }

    @Override
    protected Collection<V> createEmptyCollection() {
        return new ArrayList<>();
    }
}
