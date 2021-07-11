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
import org.dockbox.hartshorn.api.SimpleMetaProvider;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.DefaultModifiers;
import org.dockbox.hartshorn.di.MetaProviderModifier;
import org.dockbox.hartshorn.di.annotations.activate.Activator;
import org.dockbox.hartshorn.di.annotations.inject.InjectConfig;
import org.dockbox.hartshorn.sponge.event.EventBridge;
import org.dockbox.hartshorn.sponge.inject.SpongeInjector;
import org.dockbox.hartshorn.util.Reflect;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.jvm.Plugin;

@Plugin(Hartshorn.PROJECT_ID)
@Activator(
        value = Sponge8Bootstrap.class,
        prefix = Hartshorn.PACKAGE_PREFIX,
        configs = @InjectConfig(SpongeInjector.class)
)
public class Sponge8Application {

    protected static Sponge8Application instance;
    protected Runnable init;

    // Uses Sponge injection
    @Inject
    private PluginContainer container;

    public Sponge8Application() {
        Sponge8Application.instance = this;
        Exceptional.of("");
        this.init = HartshornApplication.create(Sponge8Application.class,
                DefaultModifiers.ACTIVATE_ALL,
                new MetaProviderModifier(SimpleMetaProvider::new)
        );
    }

    public static PluginContainer container() {
        return instance.container;
    }

    @Listener
    public void on(ConstructPluginEvent event) {
        this.init.run();

        for (Class<? extends EventBridge> bridge : Reflect.children(EventBridge.class)) {
            Sponge.eventManager().registerListeners(this.container, Hartshorn.context().get(bridge));
        }
    }
}
