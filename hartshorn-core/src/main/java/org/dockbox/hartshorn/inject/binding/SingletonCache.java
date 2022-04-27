package org.dockbox.hartshorn.inject.binding;

import org.dockbox.hartshorn.inject.Key;

public interface SingletonCache {

    <T> void put(Key<T> key, T instance);

    <T> T get(Key<T> key);

    <T> void remove(Key<T> key);

    <T> boolean contains(Key<T> key);

    <T> void clear();

}
