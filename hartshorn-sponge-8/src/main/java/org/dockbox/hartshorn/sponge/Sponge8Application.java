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

package org.dockbox.hartshorn.sponge;

import com.google.inject.Inject;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.HartshornApplication;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.Modifier;
import org.dockbox.hartshorn.di.annotations.Activator;
import org.dockbox.hartshorn.di.annotations.InjectConfig;
import org.dockbox.hartshorn.server.minecraft.MinecraftServerBootstrap;
import org.dockbox.hartshorn.sponge.command.SpongeCommandRegistrar;
import org.dockbox.hartshorn.sponge.inject.SpongeInjector;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.lifecycle.StartingEngineEvent;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.jvm.Plugin;

@Plugin(Hartshorn.PROJECT_ID)
@Activator(
        value = MinecraftServerBootstrap.class,
        prefix = Hartshorn.PACKAGE_PREFIX,
        configs = @InjectConfig(SpongeInjector.class)
)
public class Sponge8Application extends HartshornApplication {

    protected static Sponge8Application instance;
    protected Runnable init;

    // Uses Sponge injection
    @Inject
    private PluginContainer container;

    public Sponge8Application() {
        Sponge8Application.instance = this;
        Exceptional.of("");
        this.init = HartshornApplication.create(Sponge8Application.class, Modifier.ACTIVATE_ALL);
    }

    public static PluginContainer container() {
        return instance.container;
    }

    public void on(StartingEngineEvent<?> event) {
        Sponge.eventManager().registerListeners(this.container, new SpongeCommandRegistrar());
        this.init.run();
    }
}
