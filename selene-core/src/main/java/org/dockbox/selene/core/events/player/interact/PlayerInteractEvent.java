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

package org.dockbox.selene.core.events.player.interact;

import org.dockbox.selene.core.events.AbstractTargetCancellableEvent;
import org.dockbox.selene.core.objects.location.Location;
import org.dockbox.selene.core.objects.player.ClickType;
import org.dockbox.selene.core.objects.player.Hand;
import org.dockbox.selene.core.objects.player.Player;
import org.dockbox.selene.core.objects.player.Sneaking;
import org.dockbox.selene.core.objects.targets.Target;

public class PlayerInteractEvent extends AbstractTargetCancellableEvent
{

    private final Sneaking sneaking;
    private final Hand hand;
    private final ClickType clickType;

    protected PlayerInteractEvent(Player player, Hand hand, ClickType clickType)
    {
        super(player);
        this.sneaking = player.isSneaking() ? Sneaking.SNEAKING : Sneaking.STANDING;
        this.hand = hand;
        this.clickType = clickType;
    }

    public Sneaking getCrouching()
    {
        return this.sneaking;
    }

    public ClickType getClientClickType()
    {
        return this.clickType;
    }

    public Hand getHand()
    {
        return this.hand;
    }

    @Override
    public Player getTarget()
    {
        return (Player) super.getTarget();
    }

    @Override
    public void setTarget(Target target)
    {
        if (target instanceof Player)
            super.setTarget(target);
    }

    public static class PlayerInteractBlockEvent extends PlayerInteractEvent
    {

        private final Location blockLocation;

        public PlayerInteractBlockEvent(Player player, Hand hand, ClickType clickType, Location blockLocation)
        {
            super(player, hand, clickType);
            this.blockLocation = blockLocation;
        }

        public Location getBlockLocation()
        {
            return this.blockLocation;
        }
    }

    public static final class PlayerInteractEntityEvent extends PlayerInteractEvent
    {

        // TODO, Implementation of Entity before implementing event. See #37
        private PlayerInteractEntityEvent(Player player, Hand hand, ClickType clickType)
        {
            super(player, hand, clickType);
        }
    }

    public static class PlayerInteractAirEvent extends PlayerInteractEvent
    {

        public PlayerInteractAirEvent(Player player, Hand hand, ClickType clickType)
        {
            super(player, hand, clickType);
        }
    }
}
