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

package org.dockbox.selene.test;

import org.dockbox.selene.api.BootstrapPhase;
import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.SeleneBootstrap;
import org.dockbox.selene.api.i18n.annotations.UseResources;
import org.dockbox.selene.cache.annotations.UseCaching;
import org.dockbox.selene.commands.annotations.UseCustomArguments;
import org.dockbox.selene.config.annotations.UseConfigurations;
import org.dockbox.selene.di.InjectConfiguration;
import org.dockbox.selene.di.adapter.InjectSource;
import org.dockbox.selene.di.annotations.Activator;
import org.dockbox.selene.di.annotations.UseBeanProvision;
import org.dockbox.selene.server.Server;
import org.dockbox.selene.server.minecraft.MinecraftServerBootstrap;
import org.dockbox.selene.server.minecraft.MinecraftServerType;
import org.dockbox.selene.server.minecraft.MinecraftVersion;
import org.dockbox.selene.test.util.JUnitInjector;
import org.dockbox.selene.test.util.JUnitServer;
import org.dockbox.selene.test.util.LateJUnitInjector;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import lombok.Getter;

@Activator(inject = InjectSource.GUICE)
@UseBeanProvision
@UseCustomArguments
@UseResources
@UseConfigurations
@UseCaching
public class JUnit5Bootstrap extends MinecraftServerBootstrap {

    @Getter
    private final JUnitInformation information;

    /**
     * Instantiates {@link Selene}, creating a local injector based on the provided {@link
     * InjectConfiguration}. Also verifies dependency artifacts and injector bindings. Proceeds
     * to {@link SeleneBootstrap#construct()} once verified.
     */
    protected JUnit5Bootstrap() throws IOException {
        super(new JUnitInjector(), new LateJUnitInjector(), JUnit5Bootstrap.class);
        this.information = new JUnitInformation();
    }

    public static void prepareBootstrap() throws IOException {
        instance(null);

        JUnit5Bootstrap jUnit5Bootstrap = new JUnit5Bootstrap();
        jUnit5Bootstrap.init();
    }

    public static JUnit5Bootstrap getInstance() {
        return (JUnit5Bootstrap) SeleneBootstrap.instance();
    }

    @Override
    public @NotNull MinecraftServerType getServerType() {
        return MinecraftServerType.JUNIT;
    }

    @Override
    public String getPlatformVersion() {
        return org.junit.jupiter.api.Test.class.getPackage().getImplementationVersion();
    }

    @Override
    public MinecraftVersion getMinecraftVersion() {
        return MinecraftVersion.INDEV;
    }

    @Override
    protected void init() {
        super.enter(BootstrapPhase.PRE_INIT);
        super.init();
        super.enter(BootstrapPhase.INIT);
        this.getContext().bind(Server.class, JUnitServer.class);
    }
    
    @Override
    protected void handleMissingBinding(Class<?> type) {
        Selene.log().warn("Ignoring missing binding for " + type.getSimpleName());
    }
}
