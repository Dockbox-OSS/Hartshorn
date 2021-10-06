package org.dockbox.hartshorn.di;

import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("SuspiciousMethodCalls")
public abstract class MultiMap<K, V> {

    private final Map<K, Collection<V>> map = HartshornUtils.emptyMap();

    protected abstract Collection<V> baseCollection();

    public void put(K key, V value) {
        this.map.computeIfAbsent(key, k -> this.baseCollection()).add(value);
    }

    public void putIfAbsent(K key, V value) {
        this.map.computeIfAbsent(key, k -> this.baseCollection());
        if (!this.map.get(key).contains(value)) {
            this.map.get(key).add(value);
        }
    }

    public Collection<V> get(Object key) {
        return this.map.getOrDefault(key, this.baseCollection());
    }

    public Set<K> keySet() {
        return this.map.keySet();
    }

    public Set<Map.Entry<K, Collection<V>>> entrySet() {
        return this.map.entrySet();
    }

    public Collection<Collection<V>> values() {
        return this.map.values();
    }

    public boolean containsKey(Object key) {
        return this.map.containsKey(key);
    }

    public Collection<V> remove(Object key) {
        return this.map.remove(key);
    }

    public int size() {
        int size = 0;
        for (Collection<V> value : this.map.values()) {
            size += value.size();
        }
        return size;
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public void clear() {
        this.map.clear();
    }

    public boolean remove(K key, V value) {
        if (this.map.get(key) != null)
            return this.map.get(key).remove(value);

        return false;
    }

    public boolean replace(K key, V oldValue, V newValue) {
        if (this.map.get(key) != null) {
            if (this.map.get(key).remove(oldValue)) {
                return this.map.get(key).add(newValue);
            }
        }
        return false;
    }
}
 