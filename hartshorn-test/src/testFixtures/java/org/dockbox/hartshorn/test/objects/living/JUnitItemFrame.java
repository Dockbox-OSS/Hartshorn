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

package org.dockbox.hartshorn.test.objects.living;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.annotations.inject.Bound;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.server.minecraft.dimension.position.BlockFace;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.entities.ItemFrame;
import org.dockbox.hartshorn.server.minecraft.item.Item;

import java.util.UUID;

import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;

public class JUnitItemFrame extends JUnitEntity<ItemFrame> implements ItemFrame, org.dockbox.hartshorn.test.objects.JUnitPersistentDataHolder {

    @Setter private Item displayedItem;
    @Getter @Setter private Rotation rotation = Rotation.TOP;
    @Getter @Setter private BlockFace blockFace = BlockFace.NORTH;
    @Inject
    @Getter
    private ApplicationContext applicationContext;

    public JUnitItemFrame(final ApplicationContext context, final UUID uuid) {
        super(uuid);
    }

    @Bound
    public JUnitItemFrame(final Location location) {
        super(UUID.randomUUID());
        this.location(location);
    }

    @Override
    public Exceptional<ItemFrame> copy() {
        return Exceptional.of(new JUnitItemFrame(this.applicationContext(), UUID.randomUUID()));
    }

    @Override
    public Exceptional<Item> displayedItem() {
        return Exceptional.of(this.displayedItem);
    }
}
