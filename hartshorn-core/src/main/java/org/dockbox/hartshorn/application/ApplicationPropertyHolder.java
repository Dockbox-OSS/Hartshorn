/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.application;

import org.dockbox.hartshorn.util.Exceptional;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;

/**
 * This class is used to store application properties.
 */
public interface ApplicationPropertyHolder {

    /**
     * Indicates whether a value exists for the given key.
     * @param key The key to check
     * @return {@literal true} if the value exists, {@literal false} otherwise
     */
    boolean hasProperty(String key);

    /**
     * Attempts to obtain a single configuration value from the given key. Nested values are separated by a single
     * period symbol. For example, in the configuration (JSON) below the deepest value is accessed with
     * <code>config.nested.value</code>, returning the value 'A'
     * <pre><code>
     * {
     *   "config": {
     *     "nested": {
     *       "value": "A"
     *     }
     *   }
     * }
     * </code></pre>
     *
     * <p>Configuration values can also represent system/environment variables.
     *
     * @param key The key used to look up the value
     * @param <T> The expected type of the value
     * @return The value if it exists, or {@link Exceptional#empty()}
     */
    <T> Exceptional<T> property(String key);

    /**
     * Attempts to obtain a collection of configuration values from the given key. For example, in the configuration
     * (JSON) below the values are accessed with <code>config.values</code>, returning the values 'A', 'B', and 'C'
     * <pre><code>
     * {
     *   "config": {
     *     "values": [ "A", "B", "C" ]
     *   }
     * }
     * </code></pre>
     *
     * @param key The key used to look up the values
     * @param <T> The expected type of the values
     * @return The values if they exist, or {@link Exceptional#empty()}
     */
    <T> Exceptional<Collection<T>> properties(String key);

    /**
     * Attempts to store a single configuration value from the given key. If there is already a value associated with
     * the given key, it will be overwritten.
     @param key The key of the value to store
     @param value The value to store
     @param <T> The expected type of the value
     */
    <T> void property(String key, T value);

    /**
     * Attempts to store all configuration values from the given map. If there is already a value associated with any
     * given key, it will be overwritten.
     @param tree The map of values to store
     */
    void properties(Map<String, Object> tree);

    /**
     * Gets all configuration values as a {@link Properties} object.
     * @return The properties object
     */
    Properties properties();
}
