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

package org.dockbox.selene.sponge;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;

import org.dockbox.selene.api.SeleneApplication;
import org.dockbox.selene.api.SeleneInformation;
import org.dockbox.selene.api.events.annotations.UseEvents;
import org.dockbox.selene.api.i18n.annotations.UseResources;
import org.dockbox.selene.cache.annotations.UseCaching;
import org.dockbox.selene.commands.annotations.UseCommands;
import org.dockbox.selene.config.annotations.UseConfigurations;
import org.dockbox.selene.di.annotations.Activator;
import org.dockbox.selene.di.annotations.InjectConfig;
import org.dockbox.selene.di.annotations.InjectPhase;
import org.dockbox.selene.di.annotations.UseBeanProvision;
import org.dockbox.selene.discord.annotations.UseDiscordCommands;
import org.dockbox.selene.proxy.annotations.UseProxying;
import org.dockbox.selene.server.minecraft.MinecraftServerBootstrap;
import org.dockbox.selene.sponge.objects.composite.Composite;
import org.dockbox.selene.sponge.objects.composite.CompositeDataManipulatorBuilder;
import org.dockbox.selene.sponge.objects.composite.ImmutableCompositeData;
import org.dockbox.selene.sponge.objects.composite.MutableCompositeData;
import org.dockbox.selene.sponge.util.inject.SpongeEarlyInjector;
import org.dockbox.selene.sponge.util.inject.SpongeLateInjector;
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

/** Sponge API 7.x implementation of Selene, using events to initiate startup tasks. */
@Plugin(
        id = SeleneInformation.PROJECT_ID,
        name = SeleneInformation.PROJECT_NAME,
        description = "Custom plugins and modifications combined into a single source",
        url = "https://github.com/GuusLieben/Selene",
        authors = "GuusLieben",
        dependencies = {
                @Dependency(id = "plotsquared"),
                @Dependency(id = "nucleus"),
                @Dependency(id = "luckperms")
        })
@UseBeanProvision
@UseCommands
@UseResources
@UseConfigurations
@UseCaching
@UseDiscordCommands
@UseEvents
@UseProxying
@Activator(
        value = MinecraftServerBootstrap.class,
        prefix = SeleneInformation.PACKAGE_PREFIX,
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
     * Creates a new Selene instance using the {@link SpongeEarlyInjector}
     * bindings providing utilities.
     */
    public Sponge7Application() {
        Sponge7Application.instance = this;
        this.init = SeleneApplication.create(Sponge7Application.class);
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
