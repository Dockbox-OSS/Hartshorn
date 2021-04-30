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

package org.dockbox.selene.server.minecraft.inventory;

import org.dockbox.selene.di.annotations.AutoWired;
import org.dockbox.selene.di.annotations.Binds;
import org.dockbox.selene.server.minecraft.item.Item;
import org.dockbox.selene.server.minecraft.players.Player;

import java.util.function.Consumer;

@Binds(Element.class)
public class SimpleElement implements Element {

    private final Item item;
    private Consumer<Player> onClick;

    @AutoWired
    public SimpleElement(Item item, Consumer<Player> onClick) {
        this.item = item;
        this.onClick = onClick;
    }

    @Override
    public Item item() {
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
