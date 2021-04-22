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

package org.dockbox.selene.minecraft.entities;

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.di.SeleneFactory;
import org.dockbox.selene.minecraft.dimension.position.Location;

/**
 * A catalog of native entity types, typically representing non-custom entities which can be
 * summoned in a world.
 */
public final class EntityTypes {

    /** The Armor Stand entity type, referencing the {@link ArmorStand} entity. */
    public static final EntityType<ArmorStand> ARMOR_STAND =
            new EntityType<>(ArmorStand.class);
    /** The Item Frame entity type, referencing the {@link ItemFrame} entity. */
    public static final EntityType<ItemFrame> ITEM_FRAME = new EntityType<>(ItemFrame.class);

    private EntityTypes() {}

    /**
     * A catalog type representing the construction phase of a {@link Entity}. Typically this is only
     * used to create an entity without summoning it directly into a world.
     *
     * @param <T>
     *         The {@link Entity} to represent.
     */
    public static final class EntityType<T extends Entity> {

        private final Class<T> provider;

        EntityType(Class<T> provider) {
            this.provider = provider;
        }

        /**
         * Creates a new {@link Entity}, setting its base location to the provided {@link Location}.
         * Typically this does not summon the entity into a world.
         *
         * @param location
         *         The base location of the entity to create
         *
         * @return The new {@link Entity} instance.
         */
        public T create(Location location) {
            return Selene.provide(SeleneFactory.class).create(this.provider, location);
        }
    }
}
