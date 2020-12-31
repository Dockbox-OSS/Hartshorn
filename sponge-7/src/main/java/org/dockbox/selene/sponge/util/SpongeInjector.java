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

import com.google.inject.assistedinject.FactoryModuleBuilder;

import org.dockbox.selene.core.BroadcastService;
import org.dockbox.selene.core.DiscordUtils;
import org.dockbox.selene.core.ExceptionHelper;
import org.dockbox.selene.core.PlayerStorageService;
import org.dockbox.selene.core.ThreadUtils;
import org.dockbox.selene.core.WorldStorageService;
import org.dockbox.selene.core.annotations.files.Bulk;
import org.dockbox.selene.core.command.CommandBus;
import org.dockbox.selene.core.events.EventBus;
import org.dockbox.selene.core.extension.ExtensionManager;
import org.dockbox.selene.core.files.FileManager;
import org.dockbox.selene.core.i18n.common.ResourceService;
import org.dockbox.selene.core.impl.SimpleBroadcastService;
import org.dockbox.selene.core.impl.SimpleExceptionHelper;
import org.dockbox.selene.core.impl.SimpleResourceService;
import org.dockbox.selene.core.impl.events.SimpleEventBus;
import org.dockbox.selene.core.impl.extension.SimpleExtensionManager;
import org.dockbox.selene.core.impl.server.config.SimpleGlobalConfig;
import org.dockbox.selene.core.objects.bossbar.Bossbar;
import org.dockbox.selene.core.objects.bossbar.BossbarFactory;
import org.dockbox.selene.core.objects.item.Item;
import org.dockbox.selene.core.objects.item.ItemFactory;
import org.dockbox.selene.core.server.IntegratedExtension;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.server.SeleneInjectConfiguration;
import org.dockbox.selene.core.server.config.GlobalConfig;
import org.dockbox.selene.core.tasks.TaskRunner;
import org.dockbox.selene.core.text.pagination.PaginationBuilder;
import org.dockbox.selene.integrated.server.IntegratedServerExtension;
import org.dockbox.selene.sponge.objects.bossbar.SpongeBossbar;
import org.dockbox.selene.sponge.objects.item.SpongeItem;
import org.dockbox.selene.sponge.text.navigation.SpongePaginationBuilder;
import org.dockbox.selene.sponge.util.command.SpongeCommandBus;
import org.dockbox.selene.sponge.util.files.SpongeConfigurateManager;
import org.dockbox.selene.sponge.util.files.SpongeXStreamManager;
import org.slf4j.Logger;

public class SpongeInjector extends SeleneInjectConfiguration {

    @Override
    protected void configureExceptionInject() {
        this.bind(ExceptionHelper.class).to(SimpleExceptionHelper.class);
    }

    @Override
    protected void configureExtensionInject() {
        this.bind(ExtensionManager.class).toInstance(new SimpleExtensionManager());
        this.bind(IntegratedExtension.class).to(IntegratedServerExtension.class);
    }

    @Override
    protected void configureUtilInject() {
        this.bind(DiscordUtils.class).to(SpongeDiscordUtils.class);
        this.bind(ThreadUtils.class).to(SpongeThreadUtils.class);
    }

    @Override
    protected void configurePlatformInject() {
        this.bind(CommandBus.class).toInstance(new SpongeCommandBus());
        this.bind(FileManager.class).to(SpongeConfigurateManager.class);
        this.bind(FileManager.class).annotatedWith(Bulk.class).to(SpongeXStreamManager.class);
        this.bind(PlayerStorageService.class).to(SpongePlayerStorageService.class);
        this.bind(WorldStorageService.class).to(SpongeWorldStorageService.class);
        this.bind(TaskRunner.class).to(SpongeTaskRunner.class);
        this.bind(PaginationBuilder.class).to(SpongePaginationBuilder.class);
        this.install(new FactoryModuleBuilder().implement(Item.class, SpongeItem.class).build(ItemFactory.class));
        this.install(new FactoryModuleBuilder().implement(Bossbar.class, SpongeBossbar.class).build(BossbarFactory.class));
    }

    @Override
    protected void configureDefaultInject() {
        this.bind(BroadcastService.class).to(SimpleBroadcastService.class);
        this.bind(EventBus.class).toInstance(new SimpleEventBus());
        this.bind(GlobalConfig.class).toInstance(new SimpleGlobalConfig());
        this.bind(ResourceService.class).toInstance(new SimpleResourceService());
        this.bind(Logger.class).toInstance(Selene.log());
    }
}
