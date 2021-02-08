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

package org.dockbox.selene.api.events.player;

import org.dockbox.selene.api.events.AbstractTargetCancellableEvent;
import org.dockbox.selene.api.objects.location.Location;
import org.dockbox.selene.api.objects.location.Warp;
import org.dockbox.selene.api.objects.location.World;
import org.dockbox.selene.api.objects.targets.Target;

/**
 * The abstract type which can be used to listen to all player movement related events.
 */
public abstract class PlayerMoveEvent extends AbstractTargetCancellableEvent
{

    protected PlayerMoveEvent(Target target)
    {
        super(target);
    }

    /**
     * The event fired when a player is teleported to another location
     */
    public static class PlayerTeleportEvent extends PlayerMoveEvent
    {
        private final Location oldLocation;
        private final Location newLocation;

        public PlayerTeleportEvent(Target target, Location oldLocation, Location newLocation)
        {
            super(target);
            this.oldLocation = oldLocation;
            this.newLocation = newLocation;
        }

        public Location getOldLocation()
        {
            return this.oldLocation;
        }

        public Location getNewLocation()
        {
            return this.newLocation;
        }
    }

    /**
     * The event fired when a player is teleported to the spawn location.
     */
    // TODO: Implementation (Sponge-1.12)
    public static class PlayerSpawnEvent extends PlayerMoveEvent
    {
        private final Location spawnLocation;

        public PlayerSpawnEvent(Target target, Location spawnLocation)
        {
            super(target);
            this.spawnLocation = spawnLocation;
        }

        public Location getSpawnLocation()
        {
            return this.spawnLocation;
        }
    }

    /**
     * The event fired when a player is teleported using a {@link Warp}.
     */
    public static class PlayerWarpEvent extends PlayerMoveEvent
    {
        private final Warp warp;

        public PlayerWarpEvent(Target target, Warp warp)
        {
            super(target);
            this.warp = warp;
        }

        public Warp getWarp()
        {
            return this.warp;
        }
    }

    /**
     * The event fired when a player switches to another world. Typically this is fired after {@link PlayerTeleportEvent}.
     */
    public static class PlayerSwitchWorldEvent extends PlayerMoveEvent
    {
        private final World oldWorld;
        private final World newWorld;

        public PlayerSwitchWorldEvent(Target target, World oldWorld, World newWorld)
        {
            super(target);
            this.oldWorld = oldWorld;
            this.newWorld = newWorld;
        }

        public World getOldWorld()
        {
            return this.oldWorld;
        }

        public World getNewWorld()
        {
            return this.newWorld;
        }
    }
}
