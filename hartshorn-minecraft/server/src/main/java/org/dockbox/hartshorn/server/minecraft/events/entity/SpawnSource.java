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

package org.dockbox.hartshorn.server.minecraft.events.entity;

public enum SpawnSource {
    // Caused by a block interaction (e.g. Monster Egg)
    BLOCK,
    // Caused by breeding
    BREEDING,
    // Caused by a chunk loading in
    CHUNK,
    // Caused by a dispenser drop
    DISPENSE,
    // Caused by item drop from either a block break or a entity dropping it/being killed
    DROP,
    // Caused by a experience orb being spawned
    EXPERIENCE,
    // Caused by a block falling because of gravite (e.g. Sand)
    FALLING_BLOCK,
    // Caused by a mob spawner
    SPAWNER,
    // Caused by a automatic entity spawn (e.g. plugin/module/command)
    PLACEMENT,
    // Caused by a projectile being activated (e.g. arrow launch)
    PROJECTILE,
    // Caused by a spawn egg being used
    SPAWN_EGG,
    // Caused by a structure generating
    STRUCTURE,
    // Caused by a block of TNT being ignited
    TNT,
    // Caused by weather changes (e.g. lightning)
    WEATHER,
    // Caused by the world spawner, natural spawning
    WORLD
}
