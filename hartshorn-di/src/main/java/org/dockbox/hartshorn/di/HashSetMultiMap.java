package org.dockbox.hartshorn.di;

import java.util.Collection;
import java.util.HashSet;

public class HashSetMultiMap<K, V> extends MultiMap<K, V> {
    @Override
    protected Collection<V> baseCollection() {
        return new HashSet<>();
    }
}
