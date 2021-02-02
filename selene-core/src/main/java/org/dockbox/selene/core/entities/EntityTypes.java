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
