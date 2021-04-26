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

import com.google.inject.name.Names;

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.domain.FileTypes;
import org.dockbox.selene.api.task.TaskRunner;
import org.dockbox.selene.api.task.ThreadUtils;
import org.dockbox.selene.commands.source.DiscordCommandSource;
import org.dockbox.selene.di.InjectConfiguration;
import org.dockbox.selene.persistence.FileManager;
import org.dockbox.selene.server.Server;
import org.dockbox.selene.server.minecraft.Console;
import org.dockbox.selene.server.minecraft.bossbar.Bossbar;
import org.dockbox.selene.server.minecraft.dimension.Worlds;
import org.dockbox.selene.server.minecraft.entities.ArmorStand;
import org.dockbox.selene.server.minecraft.entities.ItemFrame;
import org.dockbox.selene.server.minecraft.item.Item;
import org.dockbox.selene.server.minecraft.item.maps.CustomMapService;
import org.dockbox.selene.server.minecraft.players.Players;
import org.dockbox.selene.server.minecraft.players.Profile;
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
import org.slf4j.Logger;

public class JUnitInjector extends InjectConfiguration {

    @Override
    protected void configure() {
        this.bind(Server.class).to(JUnitServer.class);

        // Tasks
        this.bind(TaskRunner.class).to(JUnitTaskRunner.class);
        this.bind(ThreadUtils.class).to(JUnitThreadUtils.class);

        // Persistence
        this.bind(FileManager.class).to(JUnitConfigurateManager.class);
        this.bind(FileManager.class).annotatedWith(Names.named(FileTypes.YAML)).to(JUnitConfigurateManager.class);
        this.bind(FileManager.class).annotatedWith(Names.named(FileTypes.XML)).to(JUnitXStreamManager.class);

        // Services
        this.bind(Players.class).to(JUnitPlayers.class);
        this.bind(Worlds.class).to(JUnitWorlds.class);
        this.bind(CustomMapService.class).to(JUnitCustomMapService.class);

        // Wired types - do NOT call directly!
        this.bind(Item.class).to(JUnitItem.class);
        this.bind(Bossbar.class).to(JUnitBossbar.class);
        this.bind(Profile.class).to(JUnitProfile.class);
        this.bind(ItemFrame.class).to(JUnitItemFrame.class);
        this.bind(ArmorStand.class).to(JUnitArmorStand.class);
        this.bind(DiscordCommandSource.class).to(JUnitDiscordCommandSource.class);

        // Log is created from LoggerFactory externally
        this.bind(Logger.class).toInstance(Selene.log());

        // Console is a constant singleton, to avoid recreation
        this.bind(Console.class).toInstance(new JUnitConsole());
    }
}
