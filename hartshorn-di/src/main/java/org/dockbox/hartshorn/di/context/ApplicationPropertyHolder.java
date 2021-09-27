package org.dockbox.hartshorn.di.context;

import org.dockbox.hartshorn.api.domain.Exceptional;

import java.util.Map;

public interface ApplicationPropertyHolder {

    boolean hasProperty(String key);

    /**
     * Attempts to obtain a single configuration value from the given key. Nested
     * values are separated by a single period symbol. For example, in the configuration
     * (JSON) below the deepest value is accessed with <code>config.nested.value</code>,
     * returning the value 'A'
     * <pre><code>
     *     {
     *         "config": {
     *             "nested": {
     *                 "value": "A"
     *             }
     *         }
     *     }
     * </code></pre>
     *
     * <p>Configuration values can also represent system/environment variables.
     *
     * @param key
     *         The key used to look up the value
     * @param <T>
     *         The expected type of the value
     *
     * @return The value if it exists, or {@link Exceptional#empty()}
     */
    <T> Exceptional<T> property(String key);

    <T> void property(String key, T value);
    void properties(Map<String, Object> tree);

}
