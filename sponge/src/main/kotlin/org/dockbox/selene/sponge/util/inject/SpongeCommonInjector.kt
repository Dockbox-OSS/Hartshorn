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

package org.dockbox.selene.sponge.util.inject

import org.dockbox.selene.core.command.CommandBus
import org.dockbox.selene.core.i18n.common.ResourceService
import org.dockbox.selene.core.impl.i18n.common.SimpleResourceService
import org.dockbox.selene.core.impl.server.config.DefaultGlobalConfig
import org.dockbox.selene.core.impl.util.events.SimpleEventBus
import org.dockbox.selene.core.impl.util.exceptions.SimpleExceptionHelper
import org.dockbox.selene.core.impl.util.extension.SimpleExtensionManager
import org.dockbox.selene.core.impl.util.text.SimpleBroadcastService
import org.dockbox.selene.core.server.Selene
import org.dockbox.selene.core.server.config.GlobalConfig
import org.dockbox.selene.core.util.construct.ConstructionUtil
import org.dockbox.selene.core.util.discord.DiscordUtils
import org.dockbox.selene.core.util.events.EventBus
import org.dockbox.selene.core.util.exceptions.ExceptionHelper
import org.dockbox.selene.core.util.extension.ExtensionManager
import org.dockbox.selene.core.util.files.ConfigurateManager
import org.dockbox.selene.core.util.inject.AbstractCommonInjector
import org.dockbox.selene.core.util.player.PlayerStorageService
import org.dockbox.selene.core.util.text.BroadcastService
import org.dockbox.selene.core.util.threads.ThreadUtils
import org.dockbox.selene.core.util.world.WorldStorageService
import org.dockbox.selene.integrated.IntegratedServerExtension
import org.dockbox.selene.sponge.util.construct.SpongeConstructionUtil
import org.dockbox.selene.sponge.util.command.SpongeCommandBus
import org.dockbox.selene.sponge.util.discord.SpongeDiscordUtils
import org.dockbox.selene.sponge.util.files.SpongeConfigurateManager
import org.dockbox.selene.sponge.util.player.SpongePlayerStorageService
import org.dockbox.selene.sponge.util.thread.SpongeThreadUtils
import org.dockbox.selene.sponge.util.world.SpongeWorldStorageService

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
        bind(CommandBus::class.java).to(SpongeCommandBus::class.java)
        bind(ConfigurateManager::class.java).to(SpongeConfigurateManager::class.java)
        bind(DiscordUtils::class.java).to(SpongeDiscordUtils::class.java)
        bind(EventBus::class.java).to(SimpleEventBus::class.java)
        bind(GlobalConfig::class.java).to(DefaultGlobalConfig::class.java)
        bind(Selene.IntegratedExtension::class.java).to(IntegratedServerExtension::class.java)
        bind(ResourceService::class.java).to(SimpleResourceService::class.java)
        bind(ConstructionUtil::class.java).to(SpongeConstructionUtil::class.java)
        bind(PlayerStorageService::class.java).to(SpongePlayerStorageService::class.java)
        bind(ThreadUtils::class.java).to(SpongeThreadUtils::class.java)
        bind(WorldStorageService::class.java).to(SpongeWorldStorageService::class.java)
    }
}
