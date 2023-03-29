package org.dockbox.hartshorn.util.collections;

import java.util.Map;

public interface BiMap<K, V> extends Map<K, V> {

    @SafeVarargs
    static <K, V> BiMap<K, V> ofEntries(final Entry<K, V>... entries) {
        final BiMap<K, V> map = new StandardBiMap.HashBiMap<>();
        for (final Entry<K, V> entry : entries) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    Map<V, K> inverse();
}