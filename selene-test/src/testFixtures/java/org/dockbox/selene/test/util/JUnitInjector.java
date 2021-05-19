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

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.config.GlobalConfig;
import org.dockbox.selene.api.domain.FileTypes;
import org.dockbox.selene.api.task.TaskRunner;
import org.dockbox.selene.api.task.ThreadUtils;
import org.dockbox.selene.commands.source.DiscordCommandSource;
import org.dockbox.selene.config.Configuration;
import org.dockbox.selene.config.TargetGlobalConfig;
import org.dockbox.selene.di.InjectConfiguration;
import org.dockbox.selene.di.SeleneFactory;
import org.dockbox.selene.di.SimpleSeleneFactory;
import org.dockbox.selene.di.binding.Bindings;
import org.dockbox.selene.persistence.FileManager;
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
    public void collect() {
        // Factory creation
        this.bind(SeleneFactory.class, SimpleSeleneFactory.class);

        // Tasks
        this.bind(TaskRunner.class, JUnitTaskRunner.class);
        this.bind(ThreadUtils.class, JUnitThreadUtils.class);

        // Persistence
        this.bind(FileManager.class, JUnitConfigurateManager.class);
        this.bind(FileManager.class, JUnitConfigurateManager.class, Bindings.meta(FileTypes.YAML));
        this.bind(FileManager.class, JUnitXStreamManager.class, Bindings.meta(FileTypes.XML));

        // Services
        this.bind(Players.class, JUnitPlayers.class);
        this.bind(Worlds.class, JUnitWorlds.class);
        this.bind(CustomMapService.class, JUnitCustomMapService.class);

        // Wired types - do NOT call directly!
        this.wire(Item.class, JUnitItem.class);
        this.wire(Bossbar.class, JUnitBossbar.class);
        this.wire(Profile.class, JUnitProfile.class);
        this.wire(ItemFrame.class, JUnitItemFrame.class);
        this.wire(ArmorStand.class, JUnitArmorStand.class);
        this.wire(DiscordCommandSource.class, JUnitDiscordCommandSource.class);
        this.wire(Configuration.class, JUnitConfiguration.class);

        // Log is created from LoggerFactory externally
        this.bind(Logger.class, Selene.log());

        // Console is a constant singleton, to avoid recreation
        this.bind(Console.class, new JUnitConsole());

        this.bind(GlobalConfig.class, JUnitGlobalConfig.class);
    }
}
