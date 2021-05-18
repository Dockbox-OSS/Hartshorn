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

package org.dockbox.selene.test.objects.living;

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.di.annotations.AutoWired;
import org.dockbox.selene.server.minecraft.dimension.position.BlockFace;
import org.dockbox.selene.server.minecraft.dimension.position.Location;
import org.dockbox.selene.server.minecraft.entities.ItemFrame;
import org.dockbox.selene.server.minecraft.item.Item;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

public class JUnitItemFrame extends JUnitEntity<ItemFrame> implements ItemFrame, org.dockbox.selene.test.objects.JUnitPersistentDataHolder {

    @Setter
    private Item displayedItem;
    @Getter @Setter
    private Rotation rotation = Rotation.TOP;
    @Getter @Setter
    private BlockFace blockFace = BlockFace.NORTH;

    public JUnitItemFrame(UUID uuid) {
        super(uuid);
    }

    @AutoWired
    public JUnitItemFrame(Location location) {
        super(UUID.randomUUID());
        this.setLocation(location);
    }

    @Override
    public ItemFrame copy() {
        return new JUnitItemFrame(UUID.randomUUID());
    }

    @Override
    public Exceptional<Item> getDisplayedItem() {
        return Exceptional.of(this.displayedItem);
    }
}
