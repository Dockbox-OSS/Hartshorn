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

package org.dockbox.selene.core.events.world;

import org.dockbox.selene.core.events.AbstractCancellableEvent;
import org.dockbox.selene.core.objects.location.World;
import org.dockbox.selene.core.objects.location.WorldProperties;
import org.dockbox.selene.core.objects.player.Gamemode;
import org.dockbox.selene.core.objects.tuple.Vector3N;
import org.dockbox.selene.core.util.SeleneUtils;

import java.util.Map;
import java.util.UUID;

/**
 * The abstract type which can be used to listen to all world related events.
 */
public abstract class WorldEvent extends AbstractCancellableEvent
{

    /**
     * The abstract type which can be used to listen to all world events holding a existing {@link World} instance.
     */
    public abstract static class WorldHoldingEvent extends WorldEvent
    {
        private final World world;

        protected WorldHoldingEvent(World world)
        {
            this.world = world;
        }

        public World getWorld()
        {
            return this.world;
        }
    }

    /**
     * The event fired when a world is loaded.
     */
    public static class WorldLoadEvent extends WorldHoldingEvent
    {

        public WorldLoadEvent(World world)
        {
            super(world);
        }
    }

    /**
     * The event fired when a world is unloaded.
     */
    public static class WorldUnloadEvent extends WorldEvent
    {
        private final UUID uniqueId;

        public WorldUnloadEvent(UUID uniqueId)
        {
            this.uniqueId = uniqueId;
        }

        public UUID getUniqueId()
        {
            return this.uniqueId;
        }
    }

    /**
     * The event fired when a world is being saved.
     */
    public static class WorldSaveEvent extends WorldHoldingEvent
    {

        public WorldSaveEvent(World world)
        {
            super(world);
        }
    }

    /**
     * The event fired when a new world is being created.
     */
    public static class WorldCreatingEvent extends WorldEvent
    {
        private final WorldCreatingProperties properties;

        public WorldCreatingEvent(WorldCreatingProperties properties)
        {
            this.properties = properties;
        }

        public WorldCreatingProperties getProperties()
        {
            return this.properties;
        }
    }

    /**
     * The available properties used when a world is being created or generated.
     */
    public static class WorldCreatingProperties extends WorldProperties
    {

        private final String name;
        private final UUID uniqueId;

        private final Map<String, String> rules = SeleneUtils.emptyConcurrentMap();

        public WorldCreatingProperties(String name, UUID uniqueId, boolean loadOnStartup, Vector3N spawnPosition,
                                       long seed, Gamemode defaultGamemode, Map<String, String> gamerules)
        {
            super(loadOnStartup, spawnPosition, seed, defaultGamemode);
            this.name = name;
            this.uniqueId = uniqueId;
            gamerules.forEach(this::setGamerule);
        }

        @Override
        public void setGamerule(String key, String value)
        {
            this.rules.put(key, value);
        }

        @Override
        public Map<String, String> getGamerules()
        {
            return this.rules;
        }

        public String getName()
        {
            return this.name;
        }

        public UUID getUniqueId()
        {
            return this.uniqueId;
        }

        public Map<String, String> getRules()
        {
            return this.rules;
        }
    }

}
