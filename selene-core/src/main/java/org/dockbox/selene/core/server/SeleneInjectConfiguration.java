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

package org.dockbox.selene.core.server;

import com.google.inject.AbstractModule;

import org.dockbox.selene.core.BroadcastService;
import org.dockbox.selene.core.DiscordUtils;
import org.dockbox.selene.core.ExceptionHelper;
import org.dockbox.selene.core.PlayerStorageService;
import org.dockbox.selene.core.SeleneUtils;
import org.dockbox.selene.core.events.EventBus;
import org.dockbox.selene.core.extension.ExtensionManager;
import org.dockbox.selene.core.files.ConfigurateManager;
import org.dockbox.selene.core.i18n.common.ResourceService;
import org.dockbox.selene.core.server.config.GlobalConfig;

import java.util.List;

public abstract class SeleneInjectConfiguration extends AbstractModule {

    public static final List<Class<?>> REQUIRED_BINDINGS = SeleneUtils.asList(
            ExceptionHelper.class,
            ExtensionManager.class,
            ConfigurateManager.class,
            EventBus.class,
            DiscordUtils.class,
            BroadcastService.class,
            PlayerStorageService.class,
            ResourceService.class,
            GlobalConfig.class
    );

    @Override
    protected void configure() {
        super.configure();
    }

    protected abstract void configureExceptionInject();

    protected abstract void configureExtensionInject();

    protected abstract void configureUtilInject();

    protected abstract void configurePlatformInject();

    protected abstract void configureDefaultInject();
}
