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

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.config.GlobalConfig;
import org.dockbox.hartshorn.api.task.TaskRunner;
import org.dockbox.hartshorn.api.task.ThreadUtils;
import org.dockbox.hartshorn.cache.CacheManager;
import org.dockbox.hartshorn.commands.source.DiscordCommandSource;
import org.dockbox.hartshorn.config.ConfigurationManager;
import org.dockbox.hartshorn.di.InjectConfiguration;
import org.dockbox.hartshorn.di.SimpleTypeFactory;
import org.dockbox.hartshorn.di.TypeFactory;
import org.dockbox.hartshorn.discord.DiscordUtils;
import org.dockbox.hartshorn.persistence.FileManager;
import org.dockbox.hartshorn.server.minecraft.Console;
import org.dockbox.hartshorn.server.minecraft.MinecraftVersion;
import org.dockbox.hartshorn.server.minecraft.bossbar.Bossbar;
import org.dockbox.hartshorn.server.minecraft.dimension.Worlds;
import org.dockbox.hartshorn.server.minecraft.entities.ArmorStand;
import org.dockbox.hartshorn.server.minecraft.entities.ItemFrame;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.item.maps.CustomMapService;
import org.dockbox.hartshorn.server.minecraft.players.Players;
import org.dockbox.hartshorn.server.minecraft.players.Profile;
import org.dockbox.hartshorn.test.files.JUnitFileManager;
import org.dockbox.hartshorn.test.objects.JUnitBossbar;
import org.dockbox.hartshorn.test.objects.JUnitConsole;
import org.dockbox.hartshorn.test.objects.JUnitDiscordCommandSource;
import org.dockbox.hartshorn.test.objects.JUnitItem;
import org.dockbox.hartshorn.test.objects.JUnitProfile;
import org.dockbox.hartshorn.test.objects.living.JUnitArmorStand;
import org.dockbox.hartshorn.test.objects.living.JUnitItemFrame;
import org.dockbox.hartshorn.test.services.JUnitCustomMapService;
import org.dockbox.hartshorn.test.services.JUnitPlayers;
import org.dockbox.hartshorn.test.services.JUnitWorlds;
import org.slf4j.Logger;

public class JUnitInjector extends InjectConfiguration {

    @Override
    public void collect() {
        // Factory creation
        this.bind(TypeFactory.class, SimpleTypeFactory.class);

        // Tasks
        this.bind(TaskRunner.class, JUnitTaskRunner.class);
        this.bind(ThreadUtils.class, JUnitThreadUtils.class);

        // Persistence
        this.bind(FileManager.class, JUnitFileManager.class);

        // Services
        this.bind(Players.class, JUnitPlayers.class);
        this.bind(Worlds.class, JUnitWorlds.class);
        this.bind(CustomMapService.class, JUnitCustomMapService.class);
        this.bind(CacheManager.class, JUnitCacheManager.class);

        // Wired types - do NOT call directly!
        this.wire(Item.class, JUnitItem.class);
        this.wire(Bossbar.class, JUnitBossbar.class);
        this.wire(Profile.class, JUnitProfile.class);
        this.wire(ItemFrame.class, JUnitItemFrame.class);
        this.wire(ArmorStand.class, JUnitArmorStand.class);
        this.wire(DiscordCommandSource.class, JUnitDiscordCommandSource.class);
        this.wire(ConfigurationManager.class, JUnitConfigurationManager.class);

        // Log is created from LoggerFactory externally
        this.bind(Logger.class, Hartshorn.log());

        // Console is a constant singleton, to avoid recreation
        this.bind(Console.class, new JUnitConsole());

        this.bind(GlobalConfig.class, JUnitGlobalConfig.class);
        this.bind(MinecraftVersion.class, MinecraftVersion.INDEV);

        this.bind(DiscordUtils.class, JUnitDiscordUtils.class);
    }
}
