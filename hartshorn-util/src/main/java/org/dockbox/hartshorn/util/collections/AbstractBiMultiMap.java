package org.dockbox.hartshorn.util.collections;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

public abstract class AbstractBiMultiMap<K, V> extends StandardMultiMap<K, V> implements BiMultiMap<K, V> {

    protected AbstractBiMultiMap() {
    }

    protected AbstractBiMultiMap(final MultiMap<K, V> map) {
        super(map);
    }

    @Override
    public MultiMap<V, K> inverse() {
        final Set<Entry<K, Collection<V>>> entries = this.entrySet();
        final MultiMap<V, K> inverseMap = this.createEmptyInverseMap();
        for (final Entry<K, Collection<V>> entry : entries) {
            final K key = entry.getKey();
            final Collection<V> values = entry.getValue();
            for (final V value : values) {
                inverseMap.put(value, key);
            }
        }
        return inverseMap;
    }

    protected abstract MultiMap<V, K> createEmptyInverseMap();
}
