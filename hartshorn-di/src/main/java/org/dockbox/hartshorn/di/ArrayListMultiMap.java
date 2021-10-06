package org.dockbox.hartshorn.di;

import java.util.ArrayList;
import java.util.Collection;

public class ArrayListMultiMap<K, V> extends MultiMap<K, V> {
    @Override
    protected Collection<V> baseCollection() {
        return new ArrayList<>();
    }
}
