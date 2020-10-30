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

package org.dockbox.selene.test;

import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.server.ServerType;
import org.dockbox.selene.core.util.environment.MinecraftVersion;
import org.dockbox.selene.core.util.library.LibraryArtifact;
import org.dockbox.selene.test.util.TestInjector;
import org.jetbrains.annotations.NotNull;

public class SeleneTestImpl extends Selene {

    public SeleneTestImpl() {
        super(new TestInjector());
    }

    // TODO: construct() and init()

    @Override
    public @NotNull ServerType getServerType() {
        return ServerType.JUNIT;
    }

    @Override
    protected LibraryArtifact[] getPlatformArtifacts() {
        return new LibraryArtifact[0];
    }

    @Override
    public String getPlatformVersion() {
        return "5.3.2";
    }

    @Override
    public MinecraftVersion getMinecraftVersion() {
        return MinecraftVersion.INDEV;
    }
}
