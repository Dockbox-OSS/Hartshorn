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

package org.dockbox.hartshorn.toolbinding;

import org.dockbox.hartshorn.di.context.DefaultContext;
import org.dockbox.hartshorn.server.minecraft.Interactable;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.players.ClickType;
import org.dockbox.hartshorn.server.minecraft.players.Hand;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.server.minecraft.players.Sneaking;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ToolInteractionContext extends DefaultContext {

    private final ToolInteractionEvent event;

    public Player player() {
        return this.event.player();
    }

    public Item item() {
        return this.event.item();
    }

    public ItemTool tool() {
        return this.event.tool();
    }

    public Hand hand() {
        return this.event.hand();
    }

    public ClickType type() {
        return this.event.type();
    }

    public Interactable target() {
        return this.event.target();
    }

    public Sneaking sneaking() {
        return this.event.sneaking();
    }
}
