/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.integrated.tools;

import org.dockbox.selene.core.events.AbstractCancellableEvent;
import org.dockbox.selene.core.objects.item.Item;
import org.dockbox.selene.core.objects.player.ClickType;
import org.dockbox.selene.core.objects.player.Hand;
import org.dockbox.selene.core.objects.player.Player;
import org.dockbox.selene.core.objects.player.Sneaking;

public class ToolInteractionEvent extends AbstractCancellableEvent {

    private final Player player;
    private final Item<?> item;
    private final ItemTool tool;

    private final Hand hand;
    private final ClickType type;
    private final Sneaking sneaking;

    public ToolInteractionEvent(Player player, Item<?> item, ItemTool tool, Hand hand, ClickType type, Sneaking sneaking) {
        this.player = player;
        this.item = item;
        this.tool = tool;
        this.hand = hand;
        this.type = type;
        this.sneaking = sneaking;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Item<?> getItem() {
        return this.item;
    }

    public ItemTool getTool() {
        return this.tool;
    }

    public Hand getHand() {
        return this.hand;
    }

    public ClickType getType() {
        return this.type;
    }

    public Sneaking getSneaking() {
        return this.sneaking;
    }
}
