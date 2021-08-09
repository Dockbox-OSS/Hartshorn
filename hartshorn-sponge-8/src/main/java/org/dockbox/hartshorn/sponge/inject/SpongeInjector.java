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
import org.dockbox.hartshorn.api.task.TaskRunner;
import org.dockbox.hartshorn.cache.CacheManager;
import org.dockbox.hartshorn.cache.SimpleCacheManager;
import org.dockbox.hartshorn.commands.SystemSubject;
import org.dockbox.hartshorn.config.ConfigurationManager;
import org.dockbox.hartshorn.config.SimpleConfigurationManager;
import org.dockbox.hartshorn.config.TargetGlobalConfig;
import org.dockbox.hartshorn.di.InjectConfiguration;
import org.dockbox.hartshorn.di.Key;
import org.dockbox.hartshorn.di.SimpleTypeFactory;
import org.dockbox.hartshorn.di.TypeFactory;
import org.dockbox.hartshorn.i18n.text.pagination.PaginationBuilder;
import org.dockbox.hartshorn.i18n.text.pagination.SimplePaginationBuilder;
import org.dockbox.hartshorn.persistence.FileManager;
import org.dockbox.hartshorn.server.minecraft.MinecraftVersion;
import org.dockbox.hartshorn.server.minecraft.dimension.Worlds;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.players.Players;
import org.dockbox.hartshorn.server.minecraft.players.Profile;
import org.dockbox.hartshorn.sponge.dim.SpongeWorlds;
import org.dockbox.hartshorn.sponge.game.SpongePlayers;
import org.dockbox.hartshorn.sponge.game.SpongeProfile;
import org.dockbox.hartshorn.sponge.game.SpongeSystemSubject;
import org.dockbox.hartshorn.sponge.inventory.SpongeItem;
import org.dockbox.hartshorn.sponge.util.SpongeFileManager;
import org.dockbox.hartshorn.sponge.util.SpongeTaskRunner;
import org.slf4j.Logger;

public class SpongeInjector extends InjectConfiguration {

    @Override
    public void collect() {
        // Factory creation
        this.bind(Key.of(TypeFactory.class), SimpleTypeFactory.class);

        // Tasks
        this.bind(Key.of(TaskRunner.class), SpongeTaskRunner.class);

        // Persistence
        this.bind(Key.of(FileManager.class), SpongeFileManager.class);

        // Services
        this.bind(Key.of(Players.class), SpongePlayers.class);
        this.bind(Key.of(Worlds.class), SpongeWorlds.class);
//        this.bind(Key.of(WorldEditService.class), SpongeWorldEditService.class);
//        this.bind(Key.of(CustomMapService.class), SpongeCustomMapService.class);
//        this.bind(Key.of(PlotService.class), SpongePlotSquaredService.class);
        this.bind(Key.of(CacheManager.class), SimpleCacheManager.class);

        // Builder types
        this.bind(Key.of(PaginationBuilder.class), SimplePaginationBuilder.class);
//        this.bind(Key.of(LayoutBuilder.class), SpongeLayoutBuilder.class);
//        this.bind(Key.of(PaginatedPaneBuilder.class), SpongePaginatedPaneBuilder.class);
//        this.bind(Key.of(StaticPaneBuilder.class), SpongeStaticPaneBuilder.class);

        // Wired types - do NOT call directly!
        this.manual(Key.of(Item.class), SpongeItem.class);
//        this.manual(Key.of(Bossbar.class), SpongeBossbar.class);
        this.manual(Key.of(Profile.class), SpongeProfile.class);
//        this.manual(Key.of(DiscordCommandSource.class), MagiBridgeCommandSource.class);
        this.manual(Key.of(ConfigurationManager.class), SimpleConfigurationManager.class);

        // Log is created from LoggerFactory externally
        this.bind(Key.of(Logger.class), Hartshorn.log());

        // Console is a constant singleton, to avoid recreation
        this.bind(Key.of(SystemSubject.class), SpongeSystemSubject.class);

        // Packets
//        this.bind(Key.of(ChangeGameStatePacket.class), NMSChangeGameStatePacket.class);
//        this.bind(Key.of(SpawnEntityPacket.class), NMSSpawnEntityPacket.class);

        this.bind(Key.of(GlobalConfig.class), TargetGlobalConfig.class);
        this.bind(Key.of(MinecraftVersion.class), MinecraftVersion.MC1_16);
    }
}
