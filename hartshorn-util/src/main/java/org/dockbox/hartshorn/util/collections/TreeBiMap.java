package org.dockbox.hartshorn.util.collections;

import java.util.TreeMap;

public class TreeBiMap<K, V> extends StandardBiMap<K, V> {

    public TreeBiMap() {
        super(new TreeMap<>(), new TreeMap<>());
    }
}
