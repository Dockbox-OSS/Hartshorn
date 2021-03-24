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

package org.dockbox.selene.test.util;

import com.google.inject.assistedinject.FactoryModuleBuilder;

import org.dockbox.selene.api.BroadcastService;
import org.dockbox.selene.api.ExceptionHelper;
import org.dockbox.selene.api.Players;
import org.dockbox.selene.api.ThreadUtils;
import org.dockbox.selene.api.Worlds;
import org.dockbox.selene.api.command.CommandBus;
import org.dockbox.selene.api.discord.DiscordPagination;
import org.dockbox.selene.api.discord.DiscordUtils;
import org.dockbox.selene.api.discord.templates.MessageTemplate;
import org.dockbox.selene.api.entities.ArmorStand;
import org.dockbox.selene.api.entities.EntityFactory;
import org.dockbox.selene.api.entities.ItemFrame;
import org.dockbox.selene.api.events.EventBus;
import org.dockbox.selene.api.files.FileManager;
import org.dockbox.selene.api.files.FileType;
import org.dockbox.selene.api.i18n.common.ResourceService;
import org.dockbox.selene.api.i18n.permissions.AbstractPermission;
import org.dockbox.selene.api.i18n.permissions.PermissionFactory;
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
import org.dockbox.selene.api.server.Server;
import org.dockbox.selene.api.server.Selene;
import org.dockbox.selene.api.server.SeleneInjectConfiguration;
import org.dockbox.selene.api.server.config.GlobalConfig;
import org.dockbox.selene.api.tasks.TaskRunner;
import org.dockbox.selene.api.text.pagination.PaginationBuilder;
import org.dockbox.selene.api.util.web.WebUtil;
import org.dockbox.selene.common.SimpleBroadcastService;
import org.dockbox.selene.common.SimpleExceptionHelper;
import org.dockbox.selene.common.SimpleResourceService;
import org.dockbox.selene.common.discord.SimpleDiscordPagination;
import org.dockbox.selene.common.discord.SimpleMessageTemplate;
import org.dockbox.selene.common.events.SimpleEventBus;
import org.dockbox.selene.common.i18n.Permission;
import org.dockbox.selene.common.modules.SimpleModuleManager;
import org.dockbox.selene.common.server.config.SimpleGlobalConfig;
import org.dockbox.selene.common.web.GsonWebUtil;
import org.dockbox.selene.common.web.GsonXmlWebUtil;
import org.dockbox.selene.test.files.JUnitConfigurateManager;
import org.dockbox.selene.test.files.JUnitFileManager;
import org.dockbox.selene.test.files.JUnitXStreamManager;
import org.dockbox.selene.test.services.JUnitPlayers;
import org.slf4j.Logger;

public class JUnitInjector extends SeleneInjectConfiguration {

    @Override
    protected void configure() {
        // Helper types
        this.bind(ExceptionHelper.class).to(SimpleExceptionHelper.class);
        this.bind(TaskRunner.class).to(JUnitTaskRunner.class);

        // Module management
        // Module manager keeps static references, and can thus be recreated
        this.bind(ModuleManager.class).toInstance(new SimpleModuleManager());
        this.bind(Server.class).to(JUnitServer.class);

        // Utility types
        this.bind(DiscordUtils.class).to(SpongeDiscordUtils.class);
        this.bind(ThreadUtils.class).to(SpongeThreadUtils.class);
        this.bind(WebUtil.class).to(GsonWebUtil.class);
        this.bind(WebUtil.class).annotatedWith(FileType.JSON.getFormat()).to(GsonWebUtil.class);
        this.bind(WebUtil.class).annotatedWith(FileType.XML.getFormat()).to(GsonXmlWebUtil.class);

        // File management
        this.bind(FileManager.class).to(JUnitConfigurateManager.class);
        this.bind(FileManager.class).annotatedWith(FileType.YAML.getFormat()).to(JUnitConfigurateManager.class);
        this.bind(FileManager.class).annotatedWith(FileType.XML.getFormat()).to(JUnitXStreamManager.class);

        // Services
        this.bind(Players.class).to(JUnitPlayers.class);
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

        // Builder types
        this.bind(PaginationBuilder.class).to(SpongePaginationBuilder.class);
        this.bind(LayoutBuilder.class).to(SpongeLayoutBuilder.class);
        this.bind(PaginatedPaneBuilder.class).to(SpongePaginatedPaneBuilder.class);
        this.bind(StaticPaneBuilder.class).to(SpongeStaticPaneBuilder.class);

        // Factory types
        this.install(this.factory(ElementFactory.class, Element.class, SpongeElement.class));
        this.install(this.factory(ItemFactory.class, Item.class, SpongeItem.class));
        this.install(this.factory(BossbarFactory.class, Bossbar.class, SpongeBossbar.class));
        this.install(this.factory(ProfileFactory.class, Profile.class, SpongeProfile.class));
        this.install(this.factory(PermissionFactory.class, AbstractPermission.class, Permission.class));
        this.install(new FactoryModuleBuilder()
                .implement(ItemFrame.class, SpongeItemFrame.class)
                .implement(ArmorStand.class, SpongeArmorStand.class)
                .build(EntityFactory.class));

        // Globally accessible
        // Config can be recreated, so no external tracking is required (contents obtained from file, no
        // cache writes)
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
