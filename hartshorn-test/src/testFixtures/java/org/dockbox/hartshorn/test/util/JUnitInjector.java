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

import org.dockbox.hartshorn.boot.config.GlobalConfig;
import org.dockbox.hartshorn.cache.CacheManager;
import org.dockbox.hartshorn.commands.SystemSubject;
import org.dockbox.hartshorn.di.InjectConfiguration;
import org.dockbox.hartshorn.di.Key;
import org.dockbox.hartshorn.di.annotations.context.LogExclude;
import org.dockbox.hartshorn.di.binding.Providers;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.discord.DiscordCommandSource;
import org.dockbox.hartshorn.discord.DiscordUtils;
import org.dockbox.hartshorn.persistence.FileManager;
import org.dockbox.hartshorn.test.files.JUnitFileManager;
import org.dockbox.hartshorn.test.objects.JUnitDiscordCommandSource;
import org.dockbox.hartshorn.test.objects.JUnitSystemSubject;

@LogExclude
public class JUnitInjector extends InjectConfiguration {

    @Override
    public void collect(final ApplicationContext context) {
        this.bind(Key.of(DiscordCommandSource.class), JUnitDiscordCommandSource.class);
        this.bind(Key.of(DiscordUtils.class), JUnitDiscordUtils.class);

        // Overrides
        this.hierarchy(Key.of(SystemSubject.class)).add(0, Providers.of(JUnitSystemSubject.class));
        this.hierarchy(Key.of(GlobalConfig.class)).add(0, Providers.of(JUnitGlobalConfig.class));
        this.hierarchy(Key.of(FileManager.class)).add(0, Providers.of(JUnitFileManager.class));
        this.hierarchy(Key.of(CacheManager.class)).add(0, Providers.of(JUnitCacheManager.class));
    }
}
