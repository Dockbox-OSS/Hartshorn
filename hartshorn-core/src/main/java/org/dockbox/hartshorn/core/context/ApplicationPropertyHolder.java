/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.core.context;

import org.dockbox.hartshorn.core.domain.Exceptional;

import java.util.Map;
import java.util.Properties;

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

    Properties properties();

}
