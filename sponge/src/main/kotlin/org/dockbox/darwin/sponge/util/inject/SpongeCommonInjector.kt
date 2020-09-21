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

package org.dockbox.darwin.sponge.util.inject

import org.dockbox.darwin.core.command.CommandBus
import org.dockbox.darwin.core.i18n.common.ResourceService
import org.dockbox.darwin.core.i18n.common.SimpleResourceService
import org.dockbox.darwin.core.server.config.DefaultGlobalConfig
import org.dockbox.darwin.core.server.config.GlobalConfig
import org.dockbox.darwin.core.text.navigation.PaginationService
import org.dockbox.darwin.core.util.discord.DiscordUtils
import org.dockbox.darwin.core.util.events.EventBus
import org.dockbox.darwin.core.util.events.SimpleEventBus
import org.dockbox.darwin.core.util.exceptions.ExceptionHelper
import org.dockbox.darwin.core.util.exceptions.SimpleExceptionHelper
import org.dockbox.darwin.core.util.extension.ExtensionManager
import org.dockbox.darwin.core.util.extension.SimpleExtensionManager
import org.dockbox.darwin.core.util.files.BulkDataManager
import org.dockbox.darwin.core.util.files.ConfigManager
import org.dockbox.darwin.core.util.files.DataManager
import org.dockbox.darwin.core.util.files.FileUtils
import org.dockbox.darwin.core.util.files.SQLiteBulkDataManager
import org.dockbox.darwin.core.util.files.YamlConfigManager
import org.dockbox.darwin.core.util.files.YamlDataManager
import org.dockbox.darwin.core.util.inject.AbstractCommonInjector
import org.dockbox.darwin.core.util.player.PlayerStorageService
import org.dockbox.darwin.core.util.text.BroadcastService
import org.dockbox.darwin.core.util.text.SimpleBroadcastService
import org.dockbox.darwin.core.util.world.WorldStorageService
import org.dockbox.darwin.sponge.text.navigation.SpongePaginationService
import org.dockbox.darwin.sponge.util.command.SpongeCommandBus
import org.dockbox.darwin.sponge.util.discord.SpongeDiscordUtils
import org.dockbox.darwin.sponge.util.files.SpongeFileUtils
import org.dockbox.darwin.sponge.util.player.SpongePlayerStorageService
import org.dockbox.darwin.sponge.util.world.SpongeWorldStorageService

class SpongeCommonInjector : AbstractCommonInjector() {
    override fun configureExceptionInject() {
        bind(ExceptionHelper::class.java).to(SimpleExceptionHelper::class.java)
    }

    override fun configureExtensionInject() {
        bind(ExtensionManager::class.java).to(SimpleExtensionManager::class.java)
    }

    override fun configureUtilInject() {
        // Keep this alphabatically sorted if/when adding and/or swapping bindings (based on the interface type)
        // This is for no other usage than readability
        bind(BroadcastService::class.java).to(SimpleBroadcastService::class.java)
        bind(BulkDataManager::class.java).to(SQLiteBulkDataManager::class.java)
        bind(CommandBus::class.java).to(SpongeCommandBus::class.java)
        bind(ConfigManager::class.java).to(YamlConfigManager::class.java)
        bind(DataManager::class.java).to(YamlDataManager::class.java)
        bind(DiscordUtils::class.java).to(SpongeDiscordUtils::class.java)
        bind(EventBus::class.java).to(SimpleEventBus::class.java)
        bind(FileUtils::class.java).to(SpongeFileUtils::class.java)
        bind(GlobalConfig::class.java).to(DefaultGlobalConfig::class.java)
        bind(ResourceService::class.java).to(SimpleResourceService::class.java)
        bind(PaginationService::class.java).to(SpongePaginationService::class.java)
        bind(PlayerStorageService::class.java).to(SpongePlayerStorageService::class.java)
        bind(WorldStorageService::class.java).to(SpongeWorldStorageService::class.java)
    }
}
