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

package org.dockbox.selene.test.object;

import org.dockbox.selene.core.objects.location.World;
import org.dockbox.selene.core.objects.tuple.Vector3N;
import org.dockbox.selene.core.objects.user.Gamemode;
import org.dockbox.selene.core.SeleneUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.UUID;

public class TestWorld extends World {

    private boolean isLoaded = false;

    public TestWorld(@NotNull UUID worldUniqueId, @NotNull String name) {
        super(worldUniqueId, name, false, new Vector3N(0, 0, 0), -1, Gamemode.SURVIVAL, SeleneUtils.emptyMap());
    }

    @Override
    public int getPlayerCount() {
        return new Random().nextInt(20);
    }

    @Override
    public boolean unload() {
        this.isLoaded = false;
        return true;
    }

    @Override
    public boolean load() {
        this.isLoaded = true;
        return true;
    }

    @Override
    public boolean isLoaded() {
        return this.isLoaded;
    }
}
