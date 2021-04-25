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

import org.dockbox.selene.api.GlobalConfig;
import org.dockbox.selene.api.PropertiesGlobalConfig;
import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.events.EventBus;
import org.dockbox.selene.api.events.SimpleEventBus;
import org.dockbox.selene.api.exceptions.ExceptionHelper;
import org.dockbox.selene.api.exceptions.SimpleExceptionHelper;
import org.dockbox.selene.api.i18n.ResourceService;
import org.dockbox.selene.api.i18n.SimpleResourceService;
import org.dockbox.selene.api.i18n.permissions.Permission;
import org.dockbox.selene.api.i18n.permissions.SimplePermission;
import org.dockbox.selene.api.i18n.text.pagination.PaginationBuilder;
import org.dockbox.selene.api.module.ModuleManager;
import org.dockbox.selene.api.module.ModuleOwnerLookup;
import org.dockbox.selene.api.module.SimpleModuleManager;
import org.dockbox.selene.api.task.TaskRunner;
import org.dockbox.selene.api.task.ThreadUtils;
import org.dockbox.selene.commands.CommandBus;
import org.dockbox.selene.commands.source.DiscordCommandSource;
import org.dockbox.selene.commands.values.AbstractFlagCollection;
import org.dockbox.selene.database.SQLMan;
import org.dockbox.selene.database.dialects.sqlite.SQLiteMan;
import org.dockbox.selene.di.Bindings;
import org.dockbox.selene.di.InjectConfiguration;
import org.dockbox.selene.discord.DiscordPagination;
import org.dockbox.selene.discord.DiscordUtils;
import org.dockbox.selene.discord.SimpleDiscordPagination;
import org.dockbox.selene.discord.SimpleMessageTemplate;
import org.dockbox.selene.discord.templates.MessageTemplate;
import org.dockbox.selene.nms.packets.NMSChangeGameStatePacket;
import org.dockbox.selene.nms.packets.NMSSpawnEntityPacket;
import org.dockbox.selene.persistence.FileManager;
import org.dockbox.selene.persistence.FileTypes;
import org.dockbox.selene.api.domain.OwnerLookup;
import org.dockbox.selene.plots.PlotService;
import org.dockbox.selene.server.DefaultServer;
import org.dockbox.selene.server.Server;
import org.dockbox.selene.server.minecraft.Console;
import org.dockbox.selene.server.minecraft.bossbar.Bossbar;
import org.dockbox.selene.server.minecraft.dimension.Worlds;
import org.dockbox.selene.server.minecraft.entities.ArmorStand;
import org.dockbox.selene.server.minecraft.entities.ItemFrame;
import org.dockbox.selene.server.minecraft.inventory.Element;
import org.dockbox.selene.server.minecraft.inventory.SimpleElement;
import org.dockbox.selene.server.minecraft.inventory.builder.LayoutBuilder;
import org.dockbox.selene.server.minecraft.inventory.builder.PaginatedPaneBuilder;
import org.dockbox.selene.server.minecraft.inventory.builder.StaticPaneBuilder;
import org.dockbox.selene.server.minecraft.item.Item;
import org.dockbox.selene.server.minecraft.item.maps.CustomMapService;
import org.dockbox.selene.server.minecraft.packets.real.ChangeGameStatePacket;
import org.dockbox.selene.server.minecraft.packets.real.SpawnEntityPacket;
import org.dockbox.selene.server.minecraft.players.Players;
import org.dockbox.selene.server.minecraft.players.Profile;
import org.dockbox.selene.server.minecraft.service.BroadcastService;
import org.dockbox.selene.server.minecraft.service.SimpleBroadcastService;
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
import org.dockbox.selene.web.GsonWebUtil;
import org.dockbox.selene.web.GsonXmlWebUtil;
import org.dockbox.selene.web.WebUtil;
import org.dockbox.selene.worldedit.WorldEditService;
import org.slf4j.Logger;

public class SpongeInjector extends InjectConfiguration {

    @SuppressWarnings("OverlyCoupledMethod")
    @Override
    protected final void configure() {
        // Helper types
        this.bind(ExceptionHelper.class).to(SimpleExceptionHelper.class);
        this.bind(TaskRunner.class).to(SpongeTaskRunner.class);

        // Module management
        // Module manager keeps static references, and can thus be recreated
        this.bind(ModuleManager.class).toInstance(new SimpleModuleManager());
        this.bind(Server.class).to(DefaultServer.class);
        this.bind(OwnerLookup.class).to(ModuleOwnerLookup.class);

        // Utility types
        this.bind(DiscordUtils.class).to(SpongeDiscordUtils.class);
        this.bind(ThreadUtils.class).to(SpongeThreadUtils.class);
        this.bind(WebUtil.class).to(GsonWebUtil.class);
        this.bind(WebUtil.class).annotatedWith(Bindings.meta(FileTypes.JSON)).to(GsonWebUtil.class);
        this.bind(WebUtil.class).annotatedWith(Bindings.meta(FileTypes.XML)).to(GsonXmlWebUtil.class);

        // File management
        this.bind(FileManager.class).to(SpongeConfigurateManager.class);
        this.bind(FileManager.class).annotatedWith(Bindings.meta(FileTypes.YAML)).to(SpongeConfigurateManager.class);
        this.bind(FileManager.class).annotatedWith(Bindings.meta(FileTypes.XML)).to(SpongeXStreamManager.class);
        this.bind(SQLMan.class).annotatedWith(Bindings.meta(FileTypes.SQLITE)).to(SQLiteMan.class);

        // Services
        this.bind(Players.class).to(SpongePlayers.class);
        this.bind(Worlds.class).to(SpongeWorlds.class);
        this.bind(BroadcastService.class).to(SimpleBroadcastService.class);
        this.bind(ResourceService.class).toInstance(new SimpleResourceService());
        this.bind(WorldEditService.class).to(SpongeWorldEditService.class);
        this.bind(CustomMapService.class).to(SpongeCustomMapService.class);
        this.bind(PlotService.class).to(SpongePlotSquaredService.class);

        // Internal services
        // Event- and command bus keep static references, and can thus be recreated
        this.bind(CommandBus.class).toInstance(new SpongeCommandBus());
        this.bind(EventBus.class).toInstance(new SimpleEventBus());
        this.bind(AbstractFlagCollection.class).to(SpongeFlagCollection.class);

        // Builder types
        this.bind(PaginationBuilder.class).to(SpongePaginationBuilder.class);
        this.bind(LayoutBuilder.class).to(SpongeLayoutBuilder.class);
        this.bind(PaginatedPaneBuilder.class).to(SpongePaginatedPaneBuilder.class);
        this.bind(StaticPaneBuilder.class).to(SpongeStaticPaneBuilder.class);

        // Wired types
        this.bind(Element.class).to(SimpleElement.class);
        this.bind(Item.class).to(SpongeItem.class);
        this.bind(Bossbar.class).to(SpongeBossbar.class);
        this.bind(Profile.class).to(SpongeProfile.class);
        this.bind(Permission.class).to(SimplePermission.class);
        this.bind(ItemFrame.class).to(SpongeItemFrame.class);
        this.bind(ArmorStand.class).to(SpongeArmorStand.class);
        this.bind(DiscordCommandSource.class).to(MagiBridgeCommandSource.class);

        // Globally accessible
        // Config can be recreated, so no external tracking is required (contents obtained from file, no
        // cache writes)
        this.bind(GlobalConfig.class).toInstance(new PropertiesGlobalConfig());

        // Log is created from LoggerFactory externally
        this.bind(Logger.class).toInstance(Selene.log());

        // Console is a constant singleton, to avoid
        this.bind(Console.class).toInstance(SpongeConsole.getInstance());

        // Packets
        this.bind(ChangeGameStatePacket.class).to(NMSChangeGameStatePacket.class);
        this.bind(SpawnEntityPacket.class).to(NMSSpawnEntityPacket.class);

        // Discord
        this.bind(DiscordPagination.class).to(SimpleDiscordPagination.class);
        this.bind(MessageTemplate.class).to(SimpleMessageTemplate.class);
    }
}
