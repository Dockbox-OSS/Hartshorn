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

import java.util.Map;

/**
 * Represents the properties or metadata of a world.
 */
public abstract class WorldProperties
{

    private boolean loadOnStartup;
    private Vector3N spawnPosition;
    private long seed;
    private Gamemode defaultGamemode;

    protected WorldProperties(boolean loadOnStartup, Vector3N spawnPosition, long seed, Gamemode defaultGamemode)
    {
        this.loadOnStartup = loadOnStartup;
        this.spawnPosition = spawnPosition;
        this.seed = seed;
        this.defaultGamemode = defaultGamemode;
    }

    public abstract void setGamerule(String key, String value);

    public boolean getLoadOnStartup()
    {
        return this.loadOnStartup;
    }

    public void setLoadOnStartup(boolean loadOnStartup)
    {
        this.loadOnStartup = loadOnStartup;
    }

    public Vector3N getSpawnPosition()
    {
        return this.spawnPosition;
    }

    public void setSpawnPosition(Vector3N spawnPosition)
    {
        this.spawnPosition = spawnPosition;
    }

    public long getSeed()
    {
        return this.seed;
    }

    public void setSeed(long seed)
    {
        this.seed = seed;
    }

    public Gamemode getDefaultGamemode()
    {
        return this.defaultGamemode;
    }

    public void setDefaultGamemode(Gamemode defaultGamemode)
    {
        this.defaultGamemode = defaultGamemode;
    }

    public abstract Map<String, String> getGamerules();
}
