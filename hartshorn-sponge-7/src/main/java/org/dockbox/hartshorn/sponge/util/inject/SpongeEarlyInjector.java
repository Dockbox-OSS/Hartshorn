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

package org.dockbox.hartshorn.sponge.util.inject;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.config.GlobalConfig;
import org.dockbox.hartshorn.api.i18n.text.pagination.PaginationBuilder;
import org.dockbox.hartshorn.api.task.TaskRunner;
import org.dockbox.hartshorn.api.task.ThreadUtils;
import org.dockbox.hartshorn.commands.source.DiscordCommandSource;
import org.dockbox.hartshorn.commands.values.AbstractFlagCollection;
import org.dockbox.hartshorn.config.ConfigurationManager;
import org.dockbox.hartshorn.config.SimpleConfigurationManager;
import org.dockbox.hartshorn.config.TargetGlobalConfig;
import org.dockbox.hartshorn.di.InjectConfiguration;
import org.dockbox.hartshorn.di.SimpleTypeFactory;
import org.dockbox.hartshorn.di.TypeFactory;
import org.dockbox.hartshorn.nms.packets.NMSChangeGameStatePacket;
import org.dockbox.hartshorn.nms.packets.NMSSpawnEntityPacket;
import org.dockbox.hartshorn.persistence.FileManager;
import org.dockbox.hartshorn.plots.PlotService;
import org.dockbox.hartshorn.server.minecraft.Console;
import org.dockbox.hartshorn.server.minecraft.MinecraftServerType;
import org.dockbox.hartshorn.server.minecraft.MinecraftVersion;
import org.dockbox.hartshorn.server.minecraft.bossbar.Bossbar;
import org.dockbox.hartshorn.server.minecraft.dimension.Worlds;
import org.dockbox.hartshorn.server.minecraft.entities.ArmorStand;
import org.dockbox.hartshorn.server.minecraft.entities.ItemFrame;
import org.dockbox.hartshorn.server.minecraft.inventory.builder.LayoutBuilder;
import org.dockbox.hartshorn.server.minecraft.inventory.builder.PaginatedPaneBuilder;
import org.dockbox.hartshorn.server.minecraft.inventory.builder.StaticPaneBuilder;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.item.maps.CustomMapService;
import org.dockbox.hartshorn.server.minecraft.packets.real.ChangeGameStatePacket;
import org.dockbox.hartshorn.server.minecraft.packets.real.SpawnEntityPacket;
import org.dockbox.hartshorn.server.minecraft.players.Players;
import org.dockbox.hartshorn.server.minecraft.players.Profile;
import org.dockbox.hartshorn.sponge.entities.SpongeArmorStand;
import org.dockbox.hartshorn.sponge.entities.SpongeItemFrame;
import org.dockbox.hartshorn.sponge.inventory.builder.SpongeLayoutBuilder;
import org.dockbox.hartshorn.sponge.inventory.builder.SpongePaginatedPaneBuilder;
import org.dockbox.hartshorn.sponge.inventory.builder.SpongeStaticPaneBuilder;
import org.dockbox.hartshorn.sponge.objects.SpongeProfile;
import org.dockbox.hartshorn.sponge.objects.bossbar.SpongeBossbar;
import org.dockbox.hartshorn.sponge.objects.discord.MagiBridgeCommandSource;
import org.dockbox.hartshorn.sponge.objects.item.SpongeItem;
import org.dockbox.hartshorn.sponge.objects.item.maps.SpongeCustomMapService;
import org.dockbox.hartshorn.sponge.objects.targets.SpongeConsole;
import org.dockbox.hartshorn.sponge.plotsquared.SpongePlotSquaredService;
import org.dockbox.hartshorn.sponge.text.navigation.SpongePaginationBuilder;
import org.dockbox.hartshorn.sponge.util.SpongeFileManager;
import org.dockbox.hartshorn.sponge.util.SpongePlayers;
import org.dockbox.hartshorn.sponge.util.SpongeTaskRunner;
import org.dockbox.hartshorn.sponge.util.SpongeThreadUtils;
import org.dockbox.hartshorn.sponge.util.SpongeWorldEditService;
import org.dockbox.hartshorn.sponge.util.SpongeWorlds;
import org.dockbox.hartshorn.sponge.util.command.values.SpongeFlagCollection;
import org.dockbox.hartshorn.worldedit.WorldEditService;
import org.slf4j.Logger;

public class SpongeEarlyInjector extends InjectConfiguration {

    @SuppressWarnings("OverlyCoupledMethod")
    @Override
    public final void collect() {
        // Factory creation
        this.bind(TypeFactory.class, SimpleTypeFactory.class);

        // Tasks
        this.bind(TaskRunner.class, SpongeTaskRunner.class);
        this.bind(ThreadUtils.class, SpongeThreadUtils.class);

        // Persistence
        this.bind(FileManager.class, SpongeFileManager.class);

        // Services
        this.bind(Players.class, SpongePlayers.class);
        this.bind(Worlds.class, SpongeWorlds.class);
        this.bind(WorldEditService.class, SpongeWorldEditService.class);
        this.bind(CustomMapService.class, SpongeCustomMapService.class);
        this.bind(PlotService.class, SpongePlotSquaredService.class);

        // Command services
        this.bind(AbstractFlagCollection.class, SpongeFlagCollection.class);

        // Builder types
        this.bind(PaginationBuilder.class, SpongePaginationBuilder.class);
        this.bind(LayoutBuilder.class, SpongeLayoutBuilder.class);
        this.bind(PaginatedPaneBuilder.class, SpongePaginatedPaneBuilder.class);
        this.bind(StaticPaneBuilder.class, SpongeStaticPaneBuilder.class);

        // Wired types - do NOT call directly!
        this.wire(Item.class, SpongeItem.class);
        this.wire(Bossbar.class, SpongeBossbar.class);
        this.wire(Profile.class, SpongeProfile.class);
        this.wire(ItemFrame.class, SpongeItemFrame.class);
        this.wire(ArmorStand.class, SpongeArmorStand.class);
        this.wire(DiscordCommandSource.class, MagiBridgeCommandSource.class);
        this.wire(ConfigurationManager.class, SimpleConfigurationManager.class);

        // Log is created from LoggerFactory externally
        this.bind(Logger.class, Hartshorn.log());

        // Console is a constant singleton, to avoid recreation
        this.bind(Console.class, SpongeConsole.getInstance());

        // Packets
        this.bind(ChangeGameStatePacket.class, NMSChangeGameStatePacket.class);
        this.bind(SpawnEntityPacket.class, NMSSpawnEntityPacket.class);

        this.bind(GlobalConfig.class, TargetGlobalConfig.class);

        this.bind(MinecraftServerType.class, MinecraftServerType.SPONGE);
        this.bind(MinecraftVersion.class, MinecraftVersion.MC1_12);
    }
}
