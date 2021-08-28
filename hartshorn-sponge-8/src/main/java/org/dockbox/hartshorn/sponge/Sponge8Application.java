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
import org.dockbox.hartshorn.api.HartshornLoader;
import org.dockbox.hartshorn.api.MetaProviderImpl;
import org.dockbox.hartshorn.di.DefaultModifiers;
import org.dockbox.hartshorn.di.MetaProviderModifier;
import org.dockbox.hartshorn.di.annotations.activate.Activator;
import org.dockbox.hartshorn.di.annotations.inject.InjectConfig;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.sponge.event.EventBridge;
import org.dockbox.hartshorn.sponge.game.SpongeComposite;
import org.dockbox.hartshorn.sponge.inject.SpongeInjector;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;
import org.spongepowered.api.event.lifecycle.RegisterDataEvent;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.jvm.Plugin;

@Plugin(Hartshorn.PROJECT_ID)
@Activator(value = Sponge8Bootstrap.class, configs = @InjectConfig(SpongeInjector.class))
public class Sponge8Application {

    protected static Sponge8Application instance;
    protected HartshornLoader loader;
    // Safe to expose as there will only ever be one Sponge instance
    protected ApplicationContext context;

    // Uses Sponge injection
    @Inject
    private PluginContainer container;

    public Sponge8Application() {
        Sponge8Application.instance = this;
        this.loader = HartshornApplication.lazy(Sponge8Application.class,
                DefaultModifiers.ACTIVATE_ALL,
                new MetaProviderModifier(MetaProviderImpl::new)
        );
    }

    public static PluginContainer container() {
        return instance.container;
    }

    public static ApplicationContext context() {
        return instance.context;
    }

    @Listener
    public void on(final ConstructPluginEvent event) {
        this.context = this.loader.load();

        for (final TypeContext<? extends EventBridge> bridge : this.context.environment().children(EventBridge.class)) {
            Sponge.eventManager().registerListeners(this.container, this.context.get(bridge));
        }
    }

    @Listener
    public void on(final RegisterDataEvent event) {
        final DataRegistration registration = DataRegistration.of(SpongeComposite.COMPOSITE, DataHolder.Mutable.class);
        event.register(registration);
    }
}
