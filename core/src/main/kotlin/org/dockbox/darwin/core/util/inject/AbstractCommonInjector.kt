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

package org.dockbox.darwin.core.util.inject

import com.google.inject.AbstractModule
import org.dockbox.darwin.core.i18n.common.ResourceService
import org.dockbox.darwin.core.server.config.GlobalConfig
import org.dockbox.darwin.core.util.discord.DiscordUtils
import org.dockbox.darwin.core.util.events.EventBus
import org.dockbox.darwin.core.util.exceptions.ExceptionHelper
import org.dockbox.darwin.core.util.files.ConfigManager
import org.dockbox.darwin.core.util.files.DataManager
import org.dockbox.darwin.core.util.files.FileUtils
import org.dockbox.darwin.core.util.extension.ExtensionManager
import org.dockbox.darwin.core.util.player.PlayerStorageService
import org.dockbox.darwin.core.util.text.BroadcastService

abstract class AbstractCommonInjector : AbstractModule() {

    override fun configure() {
        this.configureExceptionInject()
        this.configureModuleInject()
        this.configureUtilInject()
    }

    protected abstract fun configureExceptionInject()
    protected abstract fun configureModuleInject()
    protected abstract fun configureUtilInject()

    companion object {
        val requiredBindings: Array<Class<*>> = arrayOf(
                ExceptionHelper::class.java,
                ExtensionManager::class.java,
                FileUtils::class.java,
                ConfigManager::class.java,
                DataManager::class.java,
                EventBus::class.java,
                DiscordUtils::class.java,
                BroadcastService::class.java,
                PlayerStorageService::class.java,
                ResourceService::class.java,
                GlobalConfig::class.java
        )
    }
}
