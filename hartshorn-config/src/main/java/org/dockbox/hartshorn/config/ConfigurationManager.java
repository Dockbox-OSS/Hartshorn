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

package org.dockbox.hartshorn.config;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.Exceptional;

import java.nio.file.Path;

/**
 * Manager type responsible for accessing configuration files and obtaining values
 * through key-based accessors.
 */
public interface ConfigurationManager {

    /**
     * Creates a new {@link ConfigurationManager} for the given {@link Path}.
     *
     * @param path
     *         The path referencing the configuration file.
     *
     * @return The new {@link ConfigurationManager}
     */
    static ConfigurationManager of(Path path) {
        return Hartshorn.context().get(ConfigurationManager.class, path);
    }

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
     * @param key
     *         The key used to look up the value
     * @param <T>
     *         The expected type of the value
     *
     * @return The value if it exists, or {@link Exceptional#empty()}
     */
    <T> Exceptional<T> get(String key);

}
