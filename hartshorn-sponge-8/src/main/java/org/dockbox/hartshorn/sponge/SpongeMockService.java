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

package org.dockbox.hartshorn.sponge;

import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.di.annotations.service.Service;
import org.dockbox.hartshorn.server.minecraft.dimension.Block;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.players.Player;

@Service
@Command("mock")
public class SpongeMockService {

    @Command(value = "block",arguments = "<block{Block}>")
    public void block(Player source, Block block) {
        source.inventory().give(block);
    }

    @Command(value = "item",arguments = "<item{Item}>")
    public void item(Player source, Item item) {
        source.inventory().give(item);
    }
}
