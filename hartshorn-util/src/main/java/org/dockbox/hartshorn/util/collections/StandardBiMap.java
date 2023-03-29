package org.dockbox.hartshorn.util.collections;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public abstract class StandardBiMap<K, V> extends AbstractBiMap<K, V> {


    protected StandardBiMap(Map<K, V> forward, Map<V, K> backward) {
        super(forward, backward);
    }

    public static class HashBiMap<K, V> extends StandardBiMap<K, V> {

        public HashBiMap() {
            super(new HashMap<>(), new HashMap<>());
        }
    }

    public static class TreeBiMap<K, V> extends StandardBiMap<K, V> {

        public TreeBiMap() {
            super(new TreeMap<>(), new TreeMap<>());
        }
    }

    public static class ConcurrentHashBiMap<K, V> extends StandardBiMap<K, V> {

        public ConcurrentHashBiMap() {
            super(new ConcurrentHashMap<>(), new ConcurrentHashMap<>());
        }
    }
}
