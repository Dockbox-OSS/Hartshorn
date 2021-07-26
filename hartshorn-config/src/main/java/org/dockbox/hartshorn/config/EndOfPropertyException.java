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

/**
 * Thrown when a provided key cannot be found because it's too deeply nested. For example
 * when the key <code>a.b.c</code> is requested on the following (JSON) configuration:
 * <pre><code>
 *     {
 *         "a": {
 *             "b": "value"
 *         }
 *     }
 * </code></pre>
 */
public class EndOfPropertyException extends RuntimeException {

    public EndOfPropertyException(String property, String end) {
        super(String.format("Could not locate %s. Deepest property found is %s", property, end));
    }
}
