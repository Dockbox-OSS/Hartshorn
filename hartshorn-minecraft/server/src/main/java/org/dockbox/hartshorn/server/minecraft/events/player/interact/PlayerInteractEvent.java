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

package org.dockbox.hartshorn.server.minecraft.events.player.interact;

import org.dockbox.hartshorn.api.domain.Subject;
import org.dockbox.hartshorn.api.events.AbstractTargetCancellableEvent;
import org.dockbox.hartshorn.server.minecraft.players.ClickType;
import org.dockbox.hartshorn.server.minecraft.players.Hand;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.server.minecraft.players.Sneaking;

import lombok.Getter;

@Getter
public abstract class PlayerInteractEvent extends AbstractTargetCancellableEvent {

    private final Sneaking crouching;
    private final Hand hand;
    private final ClickType clickType;

    protected PlayerInteractEvent(Player player, Hand hand, ClickType clickType) {
        super(player);
        this.crouching = player.sneaking() ? Sneaking.SNEAKING : Sneaking.STANDING;
        this.hand = hand;
        this.clickType = clickType;
    }

    @Override
    public Player subject() {
        return (Player) super.subject();
    }

    @Override
    public PlayerInteractEvent subject(Subject subject) {
        if (subject instanceof Player) super.subject(subject);
        return this;
    }

}
