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

import org.dockbox.selene.api.MinecraftVersion;
import org.dockbox.selene.api.server.Selene;
import org.dockbox.selene.api.server.SeleneInjectConfiguration;
import org.dockbox.selene.api.server.ServerType;
import org.dockbox.selene.api.server.bootstrap.SeleneBootstrap;
import org.dockbox.selene.test.util.JUnitInjector;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class JUnit5Bootstrap extends SeleneBootstrap {

    private final JUnitInformation information;

    /**
     * Instantiates {@link Selene}, creating a local injector based on the provided {@link
     * SeleneInjectConfiguration}. Also verifies dependency artifacts and injector bindings. Proceeds
     * to {@link SeleneBootstrap#construct()} once verified.
     */
    protected JUnit5Bootstrap() throws IOException {
        super(new JUnitInjector());
        this.information = new JUnitInformation();
    }

    @Override
    public @NotNull ServerType getServerType() {
        return ServerType.JUNIT;
    }

    @Override
    public String getPlatformVersion() {
        return org.junit.jupiter.api.Test.class.getPackage().getImplementationVersion();
    }

    @Override
    public MinecraftVersion getMinecraftVersion() {
        return MinecraftVersion.INDEV;
    }

    public static JUnit5Bootstrap getInstance() {
        return (JUnit5Bootstrap) SeleneBootstrap.getInstance();
    }

    public JUnitInformation getInformation() {
        return this.information;
    }
}
