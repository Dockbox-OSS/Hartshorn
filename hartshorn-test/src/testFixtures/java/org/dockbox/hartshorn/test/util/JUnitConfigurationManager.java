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

package org.dockbox.hartshorn.test.util;

import org.dockbox.hartshorn.config.SimpleConfigurationManager;
import org.dockbox.hartshorn.di.annotations.inject.Bound;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.nio.file.Path;
import java.util.Map;

public class JUnitConfigurationManager extends SimpleConfigurationManager {

    public static Map<String, Object> cache = HartshornUtils.emptyConcurrentMap();

    @Bound
    public JUnitConfigurationManager(Path path) {
        super(path);
        // Path is typically stored to obtain values from, during testing this is not required.
    }

    public static void add(String key, Object value) {
        cache.put(key ,value);
    }

    public static void reset() {
        cache.clear();
    }

    @Override
    public Map<String, Map<String, Object>> cache() {
        return HartshornUtils.ofEntries(
                HartshornUtils.entry(this.fileKey(), cache)
        );
    }

    @Override
    protected String fileKey() {
        return "junit";
    }
}
