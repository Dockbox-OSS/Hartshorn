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

package org.dockbox.selene.server.minecraft;

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.SeleneBootstrap;
import org.dockbox.selene.api.events.EventBus;
import org.dockbox.selene.di.InjectConfiguration;
import org.dockbox.selene.di.Provider;
import org.dockbox.selene.server.ServerBootstrap;
import org.dockbox.selene.server.events.ServerInitEvent;
import org.jetbrains.annotations.NotNull;

public abstract class MinecraftServerBootstrap extends ServerBootstrap {

    /**
     * Instantiates {@link Selene}, creating a local injector based on the provided {@link
     * InjectConfiguration}. Also verifies dependency artifacts and injector bindings. Proceeds
     * to {@link SeleneBootstrap#construct()} once verified.
     *
     * @param moduleConfiguration
     *         the injector provided by the Selene implementation
     */
    protected MinecraftServerBootstrap(InjectConfiguration moduleConfiguration) {
        super(moduleConfiguration);
    }

    public static MinecraftServerBootstrap getInstance() {
        return (MinecraftServerBootstrap) ServerBootstrap.getInstance();
    }

    /**
     * Gets the server type as indicated by the {@link Selene} implementation.
     *
     * @return the server type
     */
    @NotNull
    public abstract MinecraftServerType getServerType();

    /**
     * Gets the used Minecraft version.
     *
     * @return The Minecraft version
     */
    public abstract MinecraftVersion getMinecraftVersion();

    @Override
    protected void init() {
        super.init();
        EventBus bus = Provider.provide(EventBus.class);
        bus.subscribe(this);
        bus.post(new ServerInitEvent());
    }
}
