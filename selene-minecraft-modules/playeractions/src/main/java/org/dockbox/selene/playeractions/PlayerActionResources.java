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

package org.dockbox.selene.playeractions;

import org.dockbox.selene.api.i18n.annotations.Resources;
import org.dockbox.selene.api.i18n.common.ResourceEntry;
import org.dockbox.selene.api.i18n.entry.Resource;

@Resources(module = PlayerActions.class)
public class PlayerActionResources {

    public static final ResourceEntry SPECTATOR_TELEPORT_NOT_ALLOWED = new Resource("$4You are not allowed to teleport while in spectator mode", "playeractions.spectator.notallowed");
    public static final ResourceEntry DENIED_FROM_TARGET_PLOT = new Resource("$4You are denied from the plot you are teleporting to", "playeractions.plot.denied");
    public static final ResourceEntry OUTSIDE_PLOT = new Resource("$4You are outside a plot", "playeractions.plot.outside");
    public static final ResourceEntry CANNOT_INTERACT = new Resource("$4You do not have permission to interact with entities here", "playeractions.plot.interact");
    public static final ResourceEntry CANNOT_MOVE_HERE = new Resource("$4You are not permitted to move in this world", "playeractions.rootworld");

}
