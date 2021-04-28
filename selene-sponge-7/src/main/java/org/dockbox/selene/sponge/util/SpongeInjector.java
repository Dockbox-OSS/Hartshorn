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

package org.dockbox.selene.sponge.util;

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.domain.FileTypes;
import org.dockbox.selene.api.i18n.text.pagination.PaginationBuilder;
import org.dockbox.selene.api.task.TaskRunner;
import org.dockbox.selene.api.task.ThreadUtils;
import org.dockbox.selene.commands.CommandBus;
import org.dockbox.selene.commands.source.DiscordCommandSource;
import org.dockbox.selene.commands.values.AbstractFlagCollection;
import org.dockbox.selene.di.InjectConfiguration;
import org.dockbox.selene.di.binding.Bindings;
import org.dockbox.selene.discord.DiscordUtils;
import org.dockbox.selene.nms.packets.NMSChangeGameStatePacket;
import org.dockbox.selene.nms.packets.NMSSpawnEntityPacket;
import org.dockbox.selene.persistence.FileManager;
import org.dockbox.selene.plots.PlotService;
import org.dockbox.selene.server.minecraft.Console;
import org.dockbox.selene.server.minecraft.bossbar.Bossbar;
import org.dockbox.selene.server.minecraft.dimension.Worlds;
import org.dockbox.selene.server.minecraft.entities.ArmorStand;
import org.dockbox.selene.server.minecraft.entities.ItemFrame;
import org.dockbox.selene.server.minecraft.inventory.builder.LayoutBuilder;
import org.dockbox.selene.server.minecraft.inventory.builder.PaginatedPaneBuilder;
import org.dockbox.selene.server.minecraft.inventory.builder.StaticPaneBuilder;
import org.dockbox.selene.server.minecraft.item.Item;
import org.dockbox.selene.server.minecraft.item.maps.CustomMapService;
import org.dockbox.selene.server.minecraft.packets.real.ChangeGameStatePacket;
import org.dockbox.selene.server.minecraft.packets.real.SpawnEntityPacket;
import org.dockbox.selene.server.minecraft.players.Players;
import org.dockbox.selene.server.minecraft.players.Profile;
import org.dockbox.selene.sponge.entities.SpongeArmorStand;
import org.dockbox.selene.sponge.entities.SpongeItemFrame;
import org.dockbox.selene.sponge.inventory.builder.SpongeLayoutBuilder;
import org.dockbox.selene.sponge.inventory.builder.SpongePaginatedPaneBuilder;
import org.dockbox.selene.sponge.inventory.builder.SpongeStaticPaneBuilder;
import org.dockbox.selene.sponge.objects.SpongeProfile;
import org.dockbox.selene.sponge.objects.bossbar.SpongeBossbar;
import org.dockbox.selene.sponge.objects.discord.MagiBridgeCommandSource;
import org.dockbox.selene.sponge.objects.item.SpongeItem;
import org.dockbox.selene.sponge.objects.item.maps.SpongeCustomMapService;
import org.dockbox.selene.sponge.objects.targets.SpongeConsole;
import org.dockbox.selene.sponge.plotsquared.SpongePlotSquaredService;
import org.dockbox.selene.sponge.text.navigation.SpongePaginationBuilder;
import org.dockbox.selene.sponge.util.command.SpongeCommandBus;
import org.dockbox.selene.sponge.util.command.values.SpongeFlagCollection;
import org.dockbox.selene.sponge.util.files.SpongeConfigurateManager;
import org.dockbox.selene.sponge.util.files.SpongeXStreamManager;
import org.dockbox.selene.worldedit.WorldEditService;
import org.slf4j.Logger;

public class SpongeInjector extends InjectConfiguration {

    @SuppressWarnings("OverlyCoupledMethod")
    @Override
    public final void collect() {
        // Tasks
        this.bind(TaskRunner.class, SpongeTaskRunner.class);
        this.bind(ThreadUtils.class, SpongeThreadUtils.class);

        // Persistence
        this.bind(FileManager.class, SpongeConfigurateManager.class);
        this.bind(FileManager.class, SpongeConfigurateManager.class, Bindings.meta(FileTypes.YAML));
        this.bind(FileManager.class, SpongeXStreamManager.class, Bindings.meta(FileTypes.XML));

        // Services
        this.bind(Players.class, SpongePlayers.class);
        this.bind(Worlds.class, SpongeWorlds.class);
        this.bind(WorldEditService.class, SpongeWorldEditService.class);
        this.bind(CustomMapService.class, SpongeCustomMapService.class);
        this.bind(PlotService.class, SpongePlotSquaredService.class);

        // Internal services
        // Event- and command bus keep static references, and can thus be recreated
        this.bind(CommandBus.class, new SpongeCommandBus());
        this.bind(AbstractFlagCollection.class, SpongeFlagCollection.class);

        // Builder types
        this.bind(PaginationBuilder.class, SpongePaginationBuilder.class);
        this.bind(LayoutBuilder.class, SpongeLayoutBuilder.class);
        this.bind(PaginatedPaneBuilder.class, SpongePaginatedPaneBuilder.class);
        this.bind(StaticPaneBuilder.class, SpongeStaticPaneBuilder.class);

        // Wired types - do NOT call directly!
        this.bind(Item.class, SpongeItem.class);
        this.bind(Bossbar.class, SpongeBossbar.class);
        this.bind(Profile.class, SpongeProfile.class);
        this.bind(ItemFrame.class, SpongeItemFrame.class);
        this.bind(ArmorStand.class, SpongeArmorStand.class);
        this.bind(DiscordCommandSource.class, MagiBridgeCommandSource.class);

        // Log is created from LoggerFactory externally
        this.bind(Logger.class, Selene.log());

        // Console is a constant singleton, to avoid recreation
        this.bind(Console.class, SpongeConsole.getInstance());

        // Packets
        this.bind(ChangeGameStatePacket.class, NMSChangeGameStatePacket.class);
        this.bind(SpawnEntityPacket.class, NMSSpawnEntityPacket.class);

        // Discord
        this.bind(DiscordUtils.class, SpongeDiscordUtils.class);
    }
}
