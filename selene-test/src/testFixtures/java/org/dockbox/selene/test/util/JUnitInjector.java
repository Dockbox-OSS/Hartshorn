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
import org.dockbox.selene.api.module.ModuleManager;
import org.dockbox.selene.api.module.ModuleOwnerLookup;
import org.dockbox.selene.api.module.SimpleModuleManager;
import org.dockbox.selene.api.task.TaskRunner;
import org.dockbox.selene.api.task.ThreadUtils;
import org.dockbox.selene.commands.source.DiscordCommandSource;
import org.dockbox.selene.di.Bindings;
import org.dockbox.selene.di.InjectConfiguration;
import org.dockbox.selene.discord.DiscordPagination;
import org.dockbox.selene.discord.SimpleDiscordPagination;
import org.dockbox.selene.discord.SimpleMessageTemplate;
import org.dockbox.selene.discord.templates.MessageTemplate;
import org.dockbox.selene.persistence.FileManager;
import org.dockbox.selene.persistence.FileTypes;
import org.dockbox.selene.persistence.OwnerLookup;
import org.dockbox.selene.server.Server;
import org.dockbox.selene.server.minecraft.Console;
import org.dockbox.selene.server.minecraft.bossbar.Bossbar;
import org.dockbox.selene.server.minecraft.dimension.Worlds;
import org.dockbox.selene.server.minecraft.entities.ArmorStand;
import org.dockbox.selene.server.minecraft.entities.ItemFrame;
import org.dockbox.selene.server.minecraft.inventory.Element;
import org.dockbox.selene.server.minecraft.inventory.SimpleElement;
import org.dockbox.selene.server.minecraft.item.Item;
import org.dockbox.selene.server.minecraft.item.maps.CustomMapService;
import org.dockbox.selene.server.minecraft.players.Players;
import org.dockbox.selene.server.minecraft.players.Profile;
import org.dockbox.selene.server.minecraft.service.BroadcastService;
import org.dockbox.selene.server.minecraft.service.SimpleBroadcastService;
import org.dockbox.selene.test.files.JUnitConfigurateManager;
import org.dockbox.selene.test.files.JUnitXStreamManager;
import org.dockbox.selene.test.objects.JUnitBossbar;
import org.dockbox.selene.test.objects.JUnitConsole;
import org.dockbox.selene.test.objects.JUnitDiscordCommandSource;
import org.dockbox.selene.test.objects.JUnitItem;
import org.dockbox.selene.test.objects.JUnitProfile;
import org.dockbox.selene.test.objects.living.JUnitArmorStand;
import org.dockbox.selene.test.objects.living.JUnitItemFrame;
import org.dockbox.selene.test.services.JUnitCustomMapService;
import org.dockbox.selene.test.services.JUnitPlayers;
import org.dockbox.selene.test.services.JUnitWorlds;
import org.dockbox.selene.web.GsonWebUtil;
import org.dockbox.selene.web.GsonXmlWebUtil;
import org.dockbox.selene.web.WebUtil;
import org.slf4j.Logger;

public class JUnitInjector extends InjectConfiguration {

    @Override
    protected void configure() {
        // Helper types
        this.bind(ExceptionHelper.class).to(SimpleExceptionHelper.class);
        this.bind(TaskRunner.class).to(JUnitTaskRunner.class);

        // Module management
        // Module manager keeps static references, and can thus be recreated
        this.bind(ModuleManager.class).toInstance(new SimpleModuleManager());
        this.bind(Server.class).to(JUnitServer.class);
        this.bind(OwnerLookup.class).to(ModuleOwnerLookup.class);

        // Utility types
        this.bind(ThreadUtils.class).to(JUnitThreadUtils.class);
        this.bind(WebUtil.class).to(GsonWebUtil.class);
        this.bind(WebUtil.class).annotatedWith(Bindings.meta(FileTypes.YAML)).to(GsonWebUtil.class);
        this.bind(WebUtil.class).annotatedWith(Bindings.meta(FileTypes.XML)).to(GsonXmlWebUtil.class);

        // File management
        this.bind(FileManager.class).to(JUnitConfigurateManager.class);
        this.bind(FileManager.class).annotatedWith(Bindings.meta(FileTypes.YAML)).to(JUnitConfigurateManager.class);
        this.bind(FileManager.class).annotatedWith(Bindings.meta(FileTypes.XML)).to(JUnitXStreamManager.class);

        // Services
        this.bind(Players.class).to(JUnitPlayers.class);
        this.bind(Worlds.class).to(JUnitWorlds.class);
        this.bind(BroadcastService.class).to(SimpleBroadcastService.class);
        this.bind(ResourceService.class).toInstance(new SimpleResourceService());
        this.bind(CustomMapService.class).to(JUnitCustomMapService.class);

        // Internal services
        // Event- and command bus keep static references, and can thus be recreated
//        this.bind(CommandBus.class).toInstance(new SpongeCommandBus());
        this.bind(EventBus.class).toInstance(new SimpleEventBus());

        // Factory types
        this.bind(Element.class).to(SimpleElement.class);
        this.bind(Item.class).to(JUnitItem.class);
        this.bind(Bossbar.class).to(JUnitBossbar.class);
        this.bind(Profile.class).to(JUnitProfile.class);
        this.bind(Permission.class).to(SimplePermission.class);
        this.bind(ItemFrame.class).to(JUnitItemFrame.class);
        this.bind(ArmorStand.class).to(JUnitArmorStand.class);
        this.bind(DiscordCommandSource.class).to(JUnitDiscordCommandSource.class);

        // Globally accessible
        // Config can be recreated, so no external tracking is required (contents obtained from file, no
        // cache writes)
        this.bind(GlobalConfig.class).toInstance(new PropertiesGlobalConfig());

        // Log is created from LoggerFactory externally
        this.bind(Logger.class).toInstance(Selene.log());

        // Console is a constant singleton, to avoid
        this.bind(Console.class).toInstance(new JUnitConsole());

        // Discord
        this.bind(DiscordPagination.class).to(SimpleDiscordPagination.class);
        this.bind(MessageTemplate.class).to(SimpleMessageTemplate.class);
    }
}
