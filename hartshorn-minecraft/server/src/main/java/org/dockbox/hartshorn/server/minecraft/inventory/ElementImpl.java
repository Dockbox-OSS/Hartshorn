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

package org.dockbox.hartshorn.server.minecraft.inventory;

import org.dockbox.hartshorn.api.annotations.PartialApi;
import org.dockbox.hartshorn.di.annotations.inject.Binds;
import org.dockbox.hartshorn.di.annotations.inject.Bound;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.players.Player;

import java.util.function.Consumer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Binds(Element.class)
@AllArgsConstructor(onConstructor_ = @Bound)
public class ElementImpl implements Element {

    @Getter private final Item item;
    private Consumer<Player> onClick;

    @Override
    public void onClick(final Consumer<Player> onClick) {
        this.onClick = onClick;
    }

    @PartialApi
    public void perform(final Player player) {
        this.onClick.accept(player);
    }
}
