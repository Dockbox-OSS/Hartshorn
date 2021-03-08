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

package org.dockbox.selene.sponge.inventory;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import org.dockbox.selene.api.inventory.Element;
import org.dockbox.selene.api.objects.item.Item;
import org.dockbox.selene.api.objects.player.Player;

import java.util.function.Consumer;

public class SpongeElement implements Element {

    private final Item item;
    private Consumer<Player> onClick;

    @Inject
    public SpongeElement(@Assisted Item item, @Assisted Consumer<Player> onClick) {
        this.item = item;
        this.onClick = onClick;
    }

    @Override
    public Item getItem() {
        return this.item;
    }

    @Override
    public void onClick(Consumer<Player> onClick) {
        this.onClick = onClick;
    }

    public void perform(Player player) {
        this.onClick.accept(player);
    }
}
