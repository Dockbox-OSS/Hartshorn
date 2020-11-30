/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.sponge.util;

import org.dockbox.selene.core.command.CommandBus;
import org.dockbox.selene.core.i18n.common.ResourceService;
import org.dockbox.selene.core.impl.i18n.common.SimpleResourceService;
import org.dockbox.selene.core.impl.server.config.DefaultGlobalConfig;
import org.dockbox.selene.core.impl.util.events.SimpleEventBus;
import org.dockbox.selene.core.impl.util.SimpleExceptionHelper;
import org.dockbox.selene.core.impl.util.extension.SimpleExtensionManager;
import org.dockbox.selene.core.impl.util.SimpleBroadcastService;
import org.dockbox.selene.core.server.IntegratedExtension;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.server.config.GlobalConfig;
import org.dockbox.selene.core.util.construct.ConstructionUtil;
import org.dockbox.selene.core.util.discord.DiscordUtils;
import org.dockbox.selene.core.util.events.EventBus;
import org.dockbox.selene.core.util.exceptions.ExceptionHelper;
import org.dockbox.selene.core.util.extension.ExtensionManager;
import org.dockbox.selene.core.util.files.ConfigurateManager;
import org.dockbox.selene.core.util.inject.SeleneInjectModule;
import org.dockbox.selene.core.util.player.PlayerStorageService;
import org.dockbox.selene.core.util.text.BroadcastService;
import org.dockbox.selene.core.util.threads.ThreadUtils;
import org.dockbox.selene.core.util.world.WorldStorageService;
import org.dockbox.selene.integrated.server.IntegratedServerExtension;
import org.dockbox.selene.sponge.util.command.SpongeCommandBus;
import org.slf4j.Logger;

public class SpongeInjector extends SeleneInjectModule {
    @Override
    protected void configureExceptionInject() {
        this.bind(ExceptionHelper.class).to(SimpleExceptionHelper.class);
    }

    @Override
    protected void configureExtensionInject() {
        this.bind(ExtensionManager.class).to(SimpleExtensionManager.class);
        this.bind(IntegratedExtension.class).to(IntegratedServerExtension.class);
    }

    @Override
    protected void configureUtilInject() {
        this.bind(DiscordUtils.class).to(SpongeDiscordUtils.class);
        this.bind(ConstructionUtil.class).to(SpongeConstructionUtil.class);
        this.bind(ThreadUtils.class).to(SpongeThreadUtils.class);
    }

    @Override
    protected void configurePlatformInject() {
        this.bind(CommandBus.class).to(SpongeCommandBus.class);
        this.bind(ConfigurateManager.class).to(SpongeConfigurateManager.class);
        this.bind(PlayerStorageService.class).to(SpongePlayerStorageService.class);
        this.bind(WorldStorageService.class).to(SpongeWorldStorageService.class);
    }

    @Override
    protected void configureDefaultInject() {
        this.bind(BroadcastService.class).to(SimpleBroadcastService.class);
        this.bind(EventBus.class).to(SimpleEventBus.class);
        this.bind(GlobalConfig.class).to(DefaultGlobalConfig.class);
        this.bind(ResourceService.class).to(SimpleResourceService.class);
        this.bind(Logger.class).toInstance(Selene.log());
    }
}
