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

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;

import org.dockbox.hartshorn.api.HartshornApplication;
import org.dockbox.hartshorn.api.HartshornInformation;
import org.dockbox.hartshorn.di.Modifier;
import org.dockbox.hartshorn.di.annotations.Activator;
import org.dockbox.hartshorn.di.annotations.InjectConfig;
import org.dockbox.hartshorn.di.annotations.InjectPhase;
import org.dockbox.hartshorn.server.minecraft.MinecraftServerBootstrap;
import org.dockbox.hartshorn.sponge.objects.composite.Composite;
import org.dockbox.hartshorn.sponge.objects.composite.CompositeDataManipulatorBuilder;
import org.dockbox.hartshorn.sponge.objects.composite.ImmutableCompositeData;
import org.dockbox.hartshorn.sponge.objects.composite.MutableCompositeData;
import org.dockbox.hartshorn.sponge.util.inject.SpongeEarlyInjector;
import org.dockbox.hartshorn.sponge.util.inject.SpongeLateInjector;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.mutable.MapValue;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

/** Sponge API 7.x implementation of Hartshorn, using events to initiate startup tasks. */
@Plugin(
        id = HartshornInformation.PROJECT_ID,
        name = HartshornInformation.PROJECT_NAME,
        description = "Custom plugins and modifications combined into a single source",
        url = "https://github.com/GuusLieben/Hartshorn",
        authors = "GuusLieben",
        dependencies = {
                @Dependency(id = "plotsquared"),
                @Dependency(id = "nucleus"),
                @Dependency(id = "luckperms")
        })
@Activator(
        value = MinecraftServerBootstrap.class,
        prefix = HartshornInformation.PACKAGE_PREFIX,
        configs = {
                @InjectConfig(SpongeEarlyInjector.class),
                @InjectConfig(value = SpongeLateInjector.class, phase = InjectPhase.LATE)
        })
public class Sponge7Application {

    protected static Sponge7Application instance;
    protected Runnable init;
    // Uses Sponge injection
    @Inject
    private PluginContainer container;

    /**
     * Creates a new Hartshorn instance using the {@link SpongeEarlyInjector}
     * bindings providing utilities.
     */
    public Sponge7Application() {
        Sponge7Application.instance = this;
        this.init = HartshornApplication.create(Sponge7Application.class, Modifier.ACTIVATE_ALL);
    }

    public static PluginContainer container() {
        return instance.container;
    }

    @SuppressWarnings({ "AnonymousInnerClassMayBeStatic", "UnstableApiUsage" })
    @Listener
    public void on(GamePreInitializationEvent event) {
        Composite.ITEM_KEY = Key.builder()
                .type(new TypeToken<MapValue<String, Object>>() {
                })
                .query(DataQuery.of(Composite.QUERY))
                .id(Composite.ID)
                .name(Composite.NAME)
                .build();

        DataRegistration.builder()
                .dataClass(MutableCompositeData.class)
                .immutableClass(ImmutableCompositeData.class)
                .builder(new CompositeDataManipulatorBuilder())
                .id(Composite.ID)
                .name(Composite.NAME)
                .build();

        Sponge.getEventManager().registerListeners(this, new BootstrapListeners());
    }
}
