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

import org.dockbox.hartshorn.api.config.GlobalConfig;
import org.dockbox.hartshorn.api.task.TaskRunner;
import org.dockbox.hartshorn.cache.CacheManager;
import org.dockbox.hartshorn.cache.CacheManagerImpl;
import org.dockbox.hartshorn.commands.SystemSubject;
import org.dockbox.hartshorn.config.ConfigurationManager;
import org.dockbox.hartshorn.config.ConfigurationManagerImpl;
import org.dockbox.hartshorn.config.TargetGlobalConfig;
import org.dockbox.hartshorn.di.InjectConfiguration;
import org.dockbox.hartshorn.di.Key;
import org.dockbox.hartshorn.di.TypeFactory;
import org.dockbox.hartshorn.di.TypeFactoryImpl;
import org.dockbox.hartshorn.di.annotations.context.LogExclude;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.i18n.text.pagination.PaginationBuilder;
import org.dockbox.hartshorn.i18n.text.pagination.PaginationBuilderImpl;
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

import java.util.function.Supplier;

@LogExclude
public class SpongeInjector extends InjectConfiguration {

    @Override
    public void collect(final ApplicationContext context) {
        this.bind(Key.of(TypeFactory.class), TypeFactoryImpl.class);
        this.bind(Key.of(TaskRunner.class), SpongeTaskRunner.class);
        this.bind(Key.of(FileManager.class), SpongeFileManager.class);
        this.bind(Key.of(Players.class), SpongePlayers.class);
        this.bind(Key.of(Worlds.class), SpongeWorlds.class);
        this.bind(Key.of(CacheManager.class), CacheManagerImpl.class);
        this.bind(Key.of(PaginationBuilder.class), PaginationBuilderImpl.class);
        this.bind(Key.of(Item.class), SpongeItem.class);
        this.bind(Key.of(Profile.class), SpongeProfile.class);
        this.bind(Key.of(ConfigurationManager.class), ConfigurationManagerImpl.class);
        this.bind(Key.of(SystemSubject.class), SpongeSystemSubject.class);
        this.bind(Key.of(GlobalConfig.class), TargetGlobalConfig.class);
        this.bind(Key.of(MinecraftVersion.class), MinecraftVersion.MC1_16);
        this.bind(Key.of(Logger.class), (Supplier<Logger>) context::log);
    }
}
