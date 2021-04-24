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

package org.dockbox.selene.oldplots;

import org.dockbox.selene.api.i18n.annotations.Resources;
import org.dockbox.selene.api.i18n.common.ResourceEntry;
import org.dockbox.selene.api.i18n.entry.Resource;

@Resources(OldPlotsModule.class)
public final class OldPlotsResources {

    public static final ResourceEntry SINGLE_PLOT = new Resource("$3 - $1{0}$2, $1{1}, {2}", "oldplots.list.single");
    public static final ResourceEntry PLOT_HOVER = new Resource("$2Teleport to $1{0}$2, $1{1}, {2}", "oldplots.list.hover");
    public static final ResourceEntry LIST_TITLE = new Resource("$1OldPlots for $2{0}", "oldplots.list.title");
    public static final ResourceEntry ERROR_WORLDS = new Resource("$4Worlds are not stored as OldPlots", "oldplots.caught.worlds");
    public static final ResourceEntry ERROR_CALCULATION = new Resource("$4Could not calculate plot location", "oldplots.caught.calculation");
    public static final ResourceEntry ERROR_NO_LOCATION = new Resource("$4No world location configured for '{0}'", "oldplots.caught.location");
    public static final ResourceEntry ERROR_NO_PLOT = new Resource("$4No plot with that ID found", "oldplots.caught.plot");
    public static final ResourceEntry ERROR_NO_PLAYER = new Resource("$4No valid player provided", "oldplots.caught.player");

    private OldPlotsResources() {}
}
