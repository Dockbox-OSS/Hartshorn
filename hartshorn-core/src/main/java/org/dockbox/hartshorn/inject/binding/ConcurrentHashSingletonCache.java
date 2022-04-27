package org.dockbox.hartshorn.inject.binding;

import org.dockbox.hartshorn.inject.Key;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashSingletonCache implements SingletonCache {

    private final Map<Key<?>, Object> cache = new ConcurrentHashMap<>();

    @Override
    public <T> void put(final Key<T> key, final T instance) {
        this.cache.put(key, instance);
    }

    @Override
    public <T> T get(final Key<T> key) {
        final Object object = this.cache.get(key);
        if (object == null) {
            return null;
        }
        return (T) object;
    }

    @Override
    public <T> void remove(final Key<T> key) {
        this.cache.remove(key);
    }

    @Override
    public <T> boolean contains(final Key<T> key) {
        return this.cache.containsKey(key);
    }

    @Override
    public <T> void clear() {
        this.cache.clear();
    }
}
