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

package org.dockbox.selene.api.objects.location;

import org.dockbox.selene.api.objects.player.Gamemode;
import org.dockbox.selene.api.objects.tuple.Vector3N;
import org.dockbox.selene.api.util.SeleneUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public abstract class World extends WorldProperties
{

    protected UUID worldUniqueId;
    protected String name;

    public World(UUID worldUniqueId, String name, boolean loadOnStartup, @NotNull Vector3N spawnPosition, long seed, Gamemode defaultGamemode)
    {
        super(loadOnStartup, spawnPosition, seed, defaultGamemode);
        this.worldUniqueId = worldUniqueId;
        this.name = name;
    }

    public static World empty()
    {
        return new EmptyWorld();
    }

    public abstract int getPlayerCount();

    public abstract boolean unload();

    public abstract boolean load();

    public abstract boolean isLoaded();

    public UUID getWorldUniqueId()
    {
        return this.worldUniqueId;
    }

    public String getName()
    {
        return this.name;
    }

    private static final class EmptyWorld extends World
    {
        private EmptyWorld() {super(SeleneUtils.EMPTY_UUID, "Empty", false, new Vector3N(0, 0, 0), -1, Gamemode.OTHER);}

        @Override
        public int getPlayerCount()
        {
            return 0;
        }

        @Override
        public boolean unload()
        {
            return true;
        }

        @Override
        public boolean load()
        {
            return true;
        }

        @Override
        public boolean isLoaded()
        {
            return true;
        }

        @Override
        public void setGamerule(String key, String value) { }

        @Override
        public Map<String, String> getGamerules()
        {
            return SeleneUtils.emptyMap();
        }
    }
}
