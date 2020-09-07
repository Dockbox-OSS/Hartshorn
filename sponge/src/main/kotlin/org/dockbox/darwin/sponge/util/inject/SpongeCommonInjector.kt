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

import org.dockbox.darwin.core.i18n.I18nService
import org.dockbox.darwin.core.i18n.SimpleI18NService
import org.dockbox.darwin.core.server.config.DefaultGlobalConfig
import org.dockbox.darwin.core.server.config.GlobalConfig
import org.dockbox.darwin.core.util.discord.DiscordUtils
import org.dockbox.darwin.core.util.events.EventBus
import org.dockbox.darwin.core.util.events.SimpleEventBus
import org.dockbox.darwin.core.util.exceptions.ExceptionHelper
import org.dockbox.darwin.core.util.exceptions.SimpleExceptionHelper
import org.dockbox.darwin.core.util.files.*
import org.dockbox.darwin.core.util.inject.AbstractCommonInjector
import org.dockbox.darwin.core.util.extension.ExtensionManager
import org.dockbox.darwin.core.util.extension.SimpleExtensionManager
import org.dockbox.darwin.core.util.player.PlayerStorageService
import org.dockbox.darwin.core.util.text.BroadcastService
import org.dockbox.darwin.core.util.text.SimpleBroadcastService
import org.dockbox.darwin.sponge.util.discord.SpongeDiscordUtils
import org.dockbox.darwin.sponge.util.files.SpongeFileUtils
import org.dockbox.darwin.sponge.util.player.SpongePlayerStorageService

class SpongeCommonInjector : AbstractCommonInjector() {
    override fun configureExceptionInject() {
        bind(ExceptionHelper::class.java).to(SimpleExceptionHelper::class.java)
    }

    override fun configureModuleInject() {
        bind(ExtensionManager::class.java).to(SimpleExtensionManager::class.java)
    }

    override fun configureUtilInject() {
        bind(FileUtils::class.java).to(SpongeFileUtils::class.java)
        bind(ConfigManager::class.java).to(YamlConfigManager::class.java)
        bind(DataManager::class.java).to(YamlDataManager::class.java)
        bind(BulkDataManager::class.java).to(SQLiteBulkDataManager::class.java)
        bind(EventBus::class.java).to(SimpleEventBus::class.java)
        bind(DiscordUtils::class.java).to(SpongeDiscordUtils::class.java)
        bind(BroadcastService::class.java).to(SimpleBroadcastService::class.java)
        bind(PlayerStorageService::class.java).to(SpongePlayerStorageService::class.java)
        bind(I18nService::class.java).to(SimpleI18NService::class.java)
        bind(GlobalConfig::class.java).to(DefaultGlobalConfig::class.java)
    }
}
