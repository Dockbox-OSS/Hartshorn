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

package org.dockbox.hartshorn.sponge.inject;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.config.GlobalConfig;
import org.dockbox.hartshorn.i18n.text.pagination.PaginationBuilder;
import org.dockbox.hartshorn.i18n.text.pagination.SimplePaginationBuilder;
import org.dockbox.hartshorn.api.task.TaskRunner;
import org.dockbox.hartshorn.api.task.ThreadUtils;
import org.dockbox.hartshorn.cache.CacheManager;
import org.dockbox.hartshorn.cache.SimpleCacheManager;
import org.dockbox.hartshorn.config.ConfigurationManager;
import org.dockbox.hartshorn.config.SimpleConfigurationManager;
import org.dockbox.hartshorn.config.TargetGlobalConfig;
import org.dockbox.hartshorn.di.InjectConfiguration;
import org.dockbox.hartshorn.di.SimpleTypeFactory;
import org.dockbox.hartshorn.di.TypeFactory;
import org.dockbox.hartshorn.persistence.FileManager;
import org.dockbox.hartshorn.server.minecraft.Console;
import org.dockbox.hartshorn.server.minecraft.MinecraftVersion;
import org.dockbox.hartshorn.server.minecraft.dimension.Worlds;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.players.Players;
import org.dockbox.hartshorn.server.minecraft.players.Profile;
import org.dockbox.hartshorn.sponge.dim.SpongeWorlds;
import org.dockbox.hartshorn.sponge.game.SpongeConsole;
import org.dockbox.hartshorn.sponge.game.SpongePlayers;
import org.dockbox.hartshorn.sponge.game.SpongeProfile;
import org.dockbox.hartshorn.sponge.inventory.SpongeItem;
import org.dockbox.hartshorn.sponge.util.SpongeFileManager;
import org.dockbox.hartshorn.sponge.util.SpongeTaskRunner;
import org.dockbox.hartshorn.sponge.util.SpongeThreadUtil;
import org.slf4j.Logger;

public class SpongeInjector extends InjectConfiguration {

    @Override
    public void collect() {
        // Factory creation
        this.bind(TypeFactory.class, SimpleTypeFactory.class);

        // Tasks
        this.bind(TaskRunner.class, SpongeTaskRunner.class);
        this.bind(ThreadUtils.class, SpongeThreadUtil.class);

        // Persistence
        this.bind(FileManager.class, SpongeFileManager.class);

        // Services
        this.bind(Players.class, SpongePlayers.class);
        this.bind(Worlds.class, SpongeWorlds.class);
//        this.bind(WorldEditService.class, SpongeWorldEditService.class);
//        this.bind(CustomMapService.class, SpongeCustomMapService.class);
//        this.bind(PlotService.class, SpongePlotSquaredService.class);
        this.bind(CacheManager.class, SimpleCacheManager.class);

        // Builder types
        this.bind(PaginationBuilder.class, SimplePaginationBuilder.class);
//        this.bind(LayoutBuilder.class, SpongeLayoutBuilder.class);
//        this.bind(PaginatedPaneBuilder.class, SpongePaginatedPaneBuilder.class);
//        this.bind(StaticPaneBuilder.class, SpongeStaticPaneBuilder.class);

        // Wired types - do NOT call directly!
        this.manual(Item.class, SpongeItem.class);
//        this.manual(Bossbar.class, SpongeBossbar.class);
        this.manual(Profile.class, SpongeProfile.class);
//        this.manual(DiscordCommandSource.class, MagiBridgeCommandSource.class);
        this.manual(ConfigurationManager.class, SimpleConfigurationManager.class);

        // Log is created from LoggerFactory externally
        this.bind(Logger.class, Hartshorn.log());

        // Console is a constant singleton, to avoid recreation
        this.bind(Console.class, SpongeConsole.class);

        // Packets
//        this.bind(ChangeGameStatePacket.class, NMSChangeGameStatePacket.class);
//        this.bind(SpawnEntityPacket.class, NMSSpawnEntityPacket.class);

        this.bind(GlobalConfig.class, TargetGlobalConfig.class);
        this.bind(MinecraftVersion.class, MinecraftVersion.MC1_16);
    }
}
