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

import com.google.inject.assistedinject.FactoryModuleBuilder;

import org.dockbox.selene.api.BroadcastService;
import org.dockbox.selene.api.discord.DiscordPagination;
import org.dockbox.selene.api.discord.DiscordUtils;
import org.dockbox.selene.api.ExceptionHelper;
import org.dockbox.selene.api.PlayerStorageService;
import org.dockbox.selene.api.ThreadUtils;
import org.dockbox.selene.api.WorldStorageService;
import org.dockbox.selene.api.annotations.files.Bulk;
import org.dockbox.selene.api.annotations.files.Format;
import org.dockbox.selene.api.command.CommandBus;
import org.dockbox.selene.api.discord.templates.MessageTemplate;
import org.dockbox.selene.api.entities.ArmorStand;
import org.dockbox.selene.api.entities.EntityFactory;
import org.dockbox.selene.api.entities.ItemFrame;
import org.dockbox.selene.api.events.EventBus;
import org.dockbox.selene.api.files.FileManager;
import org.dockbox.selene.api.i18n.common.ResourceService;
import org.dockbox.selene.common.SimpleBroadcastService;
import org.dockbox.selene.common.SimpleExceptionHelper;
import org.dockbox.selene.common.SimpleResourceService;
import org.dockbox.selene.common.discord.SimpleDiscordPagination;
import org.dockbox.selene.common.discord.SimpleMessageTemplate;
import org.dockbox.selene.common.events.SimpleEventBus;
import org.dockbox.selene.common.modules.SimpleModuleManager;
import org.dockbox.selene.common.server.config.SimpleGlobalConfig;
import org.dockbox.selene.common.web.GsonWebUtil;
import org.dockbox.selene.common.web.GsonXmlWebUtil;
import org.dockbox.selene.api.inventory.Element;
import org.dockbox.selene.api.inventory.builder.LayoutBuilder;
import org.dockbox.selene.api.inventory.builder.PaginatedPaneBuilder;
import org.dockbox.selene.api.inventory.builder.StaticPaneBuilder;
import org.dockbox.selene.api.inventory.factory.ElementFactory;
import org.dockbox.selene.api.module.ModuleManager;
import org.dockbox.selene.api.objects.Console;
import org.dockbox.selene.api.objects.bossbar.Bossbar;
import org.dockbox.selene.api.objects.bossbar.BossbarFactory;
import org.dockbox.selene.api.objects.item.Item;
import org.dockbox.selene.api.objects.item.ItemFactory;
import org.dockbox.selene.api.objects.item.maps.CustomMapService;
import org.dockbox.selene.api.objects.profile.Profile;
import org.dockbox.selene.api.objects.profile.ProfileFactory;
import org.dockbox.selene.api.server.IntegratedModule;
import org.dockbox.selene.api.server.Selene;
import org.dockbox.selene.api.server.SeleneInjectConfiguration;
import org.dockbox.selene.api.server.config.GlobalConfig;
import org.dockbox.selene.api.tasks.TaskRunner;
import org.dockbox.selene.api.text.pagination.PaginationBuilder;
import org.dockbox.selene.api.util.web.WebUtil;
import org.dockbox.selene.database.SQLMan;
import org.dockbox.selene.database.dialects.sqlite.SQLiteMan;
import org.dockbox.selene.integrated.IntegratedServer;
import org.dockbox.selene.nms.packets.NMSChangeGameStatePacket;
import org.dockbox.selene.nms.packets.NMSSpawnEntityPacket;
import org.dockbox.selene.packets.ChangeGameStatePacket;
import org.dockbox.selene.packets.SpawnEntityPacket;
import org.dockbox.selene.sponge.entities.SpongeArmorStand;
import org.dockbox.selene.sponge.entities.SpongeItemFrame;
import org.dockbox.selene.sponge.inventory.SpongeElement;
import org.dockbox.selene.sponge.inventory.builder.SpongeLayoutBuilder;
import org.dockbox.selene.sponge.inventory.builder.SpongePaginatedPaneBuilder;
import org.dockbox.selene.sponge.inventory.builder.SpongeStaticPaneBuilder;
import org.dockbox.selene.sponge.objects.SpongeProfile;
import org.dockbox.selene.sponge.objects.bossbar.SpongeBossbar;
import org.dockbox.selene.sponge.objects.item.SpongeItem;
import org.dockbox.selene.sponge.objects.item.maps.SpongeCustomMapService;
import org.dockbox.selene.sponge.objects.targets.SpongeConsole;
import org.dockbox.selene.sponge.text.navigation.SpongePaginationBuilder;
import org.dockbox.selene.sponge.util.command.SpongeCommandBus;
import org.dockbox.selene.sponge.util.files.SpongeConfigurateManager;
import org.dockbox.selene.sponge.util.files.SpongeXStreamManager;
import org.dockbox.selene.worldedit.WorldEditService;
import org.slf4j.Logger;

public class SpongeInjector extends SeleneInjectConfiguration
{

    @SuppressWarnings("OverlyCoupledMethod")
    @Override
    protected void configure()
    {
        Selene.log().info("Configuring bindings for Selene, using [" + this.getClass().getCanonicalName() + "]");

        // Helper types
        this.bind(ExceptionHelper.class).to(SimpleExceptionHelper.class);
        this.bind(TaskRunner.class).to(SpongeTaskRunner.class);
        // Module management
        // Module manager keeps static references, and can thus be recreated
        this.bind(ModuleManager.class).toInstance(new SimpleModuleManager());
        this.bind(IntegratedModule.class).to(IntegratedServer.class);
        // Utility types
        this.bind(DiscordUtils.class).to(SpongeDiscordUtils.class);
        this.bind(ThreadUtils.class).to(SpongeThreadUtils.class);
        this.bind(WebUtil.class).to(GsonWebUtil.class);
        this.bind(WebUtil.class).annotatedWith(Format.Json.class).to(GsonWebUtil.class);
        this.bind(WebUtil.class).annotatedWith(Format.XML.class).to(GsonXmlWebUtil.class);
        // File management
        this.bind(FileManager.class).to(SpongeConfigurateManager.class);
        this.bind(FileManager.class).annotatedWith(Bulk.class).to(SpongeXStreamManager.class);
        this.bind(SQLMan.class).annotatedWith(Format.SQLite.class).to(SQLiteMan.class);
        // Services
        this.bind(PlayerStorageService.class).to(SpongePlayerStorageService.class);
        this.bind(WorldStorageService.class).to(SpongeWorldStorageService.class);
        this.bind(BroadcastService.class).to(SimpleBroadcastService.class);
        this.bind(ResourceService.class).toInstance(new SimpleResourceService());
        this.bind(WorldEditService.class).to(SpongeWorldEditService.class);
        this.bind(CustomMapService.class).to(SpongeCustomMapService.class);
        // Internal services
        // Event- and command bus keep static references, and can thus be recreated
        this.bind(CommandBus.class).toInstance(new SpongeCommandBus());
        this.bind(EventBus.class).toInstance(new SimpleEventBus());
        // Builder types
        this.bind(PaginationBuilder.class).to(SpongePaginationBuilder.class);
        this.bind(LayoutBuilder.class).to(SpongeLayoutBuilder.class);
        this.bind(PaginatedPaneBuilder.class).to(SpongePaginatedPaneBuilder.class);
        this.bind(StaticPaneBuilder.class).to(SpongeStaticPaneBuilder.class);
        // Factory types
        this.install(new FactoryModuleBuilder().implement(Element.class, SpongeElement.class).build(ElementFactory.class));
        this.install(new FactoryModuleBuilder().implement(Item.class, SpongeItem.class).build(ItemFactory.class));
        this.install(new FactoryModuleBuilder().implement(Bossbar.class, SpongeBossbar.class).build(BossbarFactory.class));
        this.install(new FactoryModuleBuilder().implement(Profile.class, SpongeProfile.class).build(ProfileFactory.class));
        this.install(new FactoryModuleBuilder()
                .implement(ItemFrame.class, SpongeItemFrame.class)
                .implement(ArmorStand.class, SpongeArmorStand.class)
                .build(EntityFactory.class)
        );
        // Globally accessible
        // Config can be recreated, so no external tracking is required (contents obtained from file, no cache writes)
        this.bind(GlobalConfig.class).toInstance(new SimpleGlobalConfig());
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
