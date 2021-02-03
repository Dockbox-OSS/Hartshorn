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

package org.dockbox.selene.core.entities;

import org.dockbox.selene.core.objects.location.Location;
import org.dockbox.selene.core.server.Selene;

import java.util.function.BiFunction;

public class EntityTypes {

    public static final EntityType<ArmorStand> ARMOR_STAND = new EntityType<>(EntityFactory::armorStand);
    public static final EntityType<ItemFrame> ITEM_FRAME = new EntityType<>(EntityFactory::itemFrame);

    public static final class EntityType<T extends Entity<T>> {

        private final BiFunction<EntityFactory, Location, T> provider;

        EntityType(BiFunction<EntityFactory, Location, T> provider) {
            this.provider = provider;
        }

        public T create(Location location) {
            return this.provider.apply(Selene.provide(EntityFactory.class), location);
        }

    }
}
