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

package org.dockbox.hartshorn.keys;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.keys.Key;
import org.dockbox.hartshorn.api.keys.Keys;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Map;

public final class TestKeys {

    private static final Map<Object, String> localRegistry = HartshornUtils.emptyMap();

    public static final Key<TestKeyHolder, String> HOLDER_KEY = Keys.of(
            localRegistry::put,
            testKeyHolder -> Exceptional.of(localRegistry.getOrDefault(testKeyHolder, null)));

    public static final Key<TestNonKeyHolder, String> NON_HOLDER_KEY = Keys.of(
            localRegistry::put,
            testKeyHolder -> Exceptional.of(localRegistry.getOrDefault(testKeyHolder, null)));

    private TestKeys() {
    }
}
