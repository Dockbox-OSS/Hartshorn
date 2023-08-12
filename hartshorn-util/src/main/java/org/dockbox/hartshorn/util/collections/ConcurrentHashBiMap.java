package org.dockbox.hartshorn.util.collections;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashBiMap<K, V> extends StandardBiMap<K, V> {

    public ConcurrentHashBiMap() {
        super(new ConcurrentHashMap<>(), new ConcurrentHashMap<>());
    }
}
