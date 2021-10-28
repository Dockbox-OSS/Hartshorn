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

package org.dockbox.hartshorn.testsuite;

import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.InjectConfiguration;
import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.annotations.context.LogExclude;
import org.dockbox.hartshorn.core.binding.Provider;
import org.dockbox.hartshorn.core.binding.Providers;
import org.dockbox.hartshorn.core.boot.config.GlobalConfig;
import org.dockbox.hartshorn.core.context.ApplicationContext;

import java.util.Map;

@LogExclude
public class JUnitInjector extends InjectConfiguration {

    private static final Map<Key<?>, Provider<?>> providers = HartshornUtils.emptyConcurrentMap();

    @Override
    public void collect(final ApplicationContext context) {
        // Overrides
        this.hierarchy(Key.of(GlobalConfig.class)).add(0, Providers.of(JUnitGlobalConfig.class));
        providers.forEach((key, provider) -> {
            final Key<Object> safeKey = (Key<Object>) key;
            final Provider<Object> safeProvider = (Provider<Object>) provider;
            this.hierarchy(safeKey).add(0, safeProvider);
        });
    }

    public static <T> void register(final Key<T> key, final Provider<T> provider) {
        providers.put(key, provider);
    }
}
