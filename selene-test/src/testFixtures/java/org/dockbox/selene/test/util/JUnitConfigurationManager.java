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

package org.dockbox.selene.test.util;

import org.dockbox.selene.config.SimpleConfigurationManager;
import org.dockbox.selene.di.annotations.Wired;
import org.dockbox.selene.util.SeleneUtils;

import java.nio.file.Path;
import java.util.Map;

public class JUnitConfigurationManager extends SimpleConfigurationManager {

    private static Map<String, Object> cache = SeleneUtils.emptyConcurrentMap();

    @Wired
    public JUnitConfigurationManager(Path path) {
        super(path);
        // Path is typically stored to obtain values from, during testing this is not required.
    }

    public static void add(String key, Object value) {
        cache.put(key ,value);
    }

    @Override
    public Map<String, Map<String, Object>> getCache() {
        return SeleneUtils.ofEntries(
                SeleneUtils.entry(this.getFileKey(), cache)
        );
    }

    @Override
    protected String getFileKey() {
        return "junit";
    }
}
