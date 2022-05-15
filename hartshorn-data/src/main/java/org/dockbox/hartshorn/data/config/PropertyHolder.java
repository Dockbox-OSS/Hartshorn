package org.dockbox.hartshorn.data.config;

import org.dockbox.hartshorn.util.Exceptional;
import org.dockbox.hartshorn.util.GenericType;

import java.util.Map;
import java.util.Properties;

public interface PropertyHolder {

    boolean has(String key);

    <T> Exceptional<T> get(String key, Class<T> type);

    <T> Exceptional<T> get(String key, GenericType<T> type);

    default <T> Exceptional<T> get(final String key) {
        return this.get(key, (Class<T>) null);
    }

    <T> void set(String key, T value);

    void set(Map<String, Object> tree);

    Properties properties();
}
