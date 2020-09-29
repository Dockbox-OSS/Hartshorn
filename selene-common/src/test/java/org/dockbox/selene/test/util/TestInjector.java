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

package org.dockbox.selene.test.util;

import org.dockbox.selene.core.command.CommandBus;
import org.dockbox.selene.core.i18n.common.ResourceService;
import org.dockbox.selene.core.impl.i18n.common.SimpleResourceService;
import org.dockbox.selene.core.impl.server.config.DefaultGlobalConfig;
import org.dockbox.selene.core.impl.util.events.SimpleEventBus;
import org.dockbox.selene.core.impl.util.exceptions.SimpleExceptionHelper;
import org.dockbox.selene.core.impl.util.extension.SimpleExtensionManager;
import org.dockbox.selene.core.impl.util.files.SQLiteBulkDataManager;
import org.dockbox.selene.core.impl.util.files.YamlConfigManager;
import org.dockbox.selene.core.impl.util.files.YamlDataManager;
import org.dockbox.selene.core.impl.util.text.SimpleBroadcastService;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.server.config.GlobalConfig;
import org.dockbox.selene.core.text.navigation.PaginationService;
import org.dockbox.selene.core.util.discord.DiscordUtils;
import org.dockbox.selene.core.util.events.EventBus;
import org.dockbox.selene.core.util.exceptions.ExceptionHelper;
import org.dockbox.selene.core.util.extension.ExtensionManager;
import org.dockbox.selene.core.util.files.BulkDataManager;
import org.dockbox.selene.core.util.files.ConfigManager;
import org.dockbox.selene.core.util.files.DataManager;
import org.dockbox.selene.core.util.files.FileUtils;
import org.dockbox.selene.core.util.inject.AbstractCommonInjector;
import org.dockbox.selene.core.util.player.PlayerStorageService;
import org.dockbox.selene.core.util.text.BroadcastService;
import org.dockbox.selene.core.util.threads.ThreadUtils;
import org.dockbox.selene.core.util.world.WorldStorageService;
import org.dockbox.selene.test.extension.IntegratedTestExtension;

public class TestInjector extends AbstractCommonInjector {

    @Override
    protected void configureExceptionInject() {
        super.bind(ExceptionHelper.class).to(SimpleExceptionHelper.class);
    }

    @Override
    protected void configureExtensionInject() {
        super.bind(ExtensionManager.class).to(SimpleExtensionManager.class);
    }

    @Override
    protected void configureUtilInject() {
        super.bind(BroadcastService.class).to(SimpleBroadcastService.class);
        super.bind(BulkDataManager.class).to(SQLiteBulkDataManager.class);
        super.bind(CommandBus.class).to(TestCommandBus.class);
        super.bind(ConfigManager.class).to(YamlConfigManager.class);
        super.bind(DataManager.class).to(YamlDataManager.class);
        super.bind(DiscordUtils.class).to(TestDiscordUtils.class);
        super.bind(EventBus.class).to(SimpleEventBus.class);
        super.bind(FileUtils.class).to(TestFileUtils.class);
        super.bind(GlobalConfig.class).to(DefaultGlobalConfig.class);
        super.bind(Selene.IntegratedExtension.class).to(IntegratedTestExtension.class);
        super.bind(ResourceService.class).to(SimpleResourceService.class);
        super.bind(PaginationService.class).to(TestPaginationService.class);
        super.bind(PlayerStorageService.class).to(TestPlayerStorageService.class);
        super.bind(ThreadUtils.class).to(TestThreadUtils.class);
        super.bind(WorldStorageService.class).to(TestWorldStorageService.class);
    }
}
