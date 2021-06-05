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

package org.dockbox.hartshorn.plots;

import org.dockbox.hartshorn.api.keys.Key;

public enum PlotProperties {
    // The filling of the plot between bedrock (if present) and the plot floor
    FILLING(PlotKeys.FILLING),
    // The plot floor
    FLOOR(PlotKeys.FLOOR),
    // The filling of the plot between the plot floor and the build height limit
    AIR(PlotKeys.AIR),
    // The filling of the entire plot
    ALL(PlotKeys.ALL),
    // The filling of the top of the plot wall
    WALL_BORDER(PlotKeys.WALL_BORDER),
    // The filling of the plot wall between bedrock (if present) and the wall border
    WALL_FILLING(PlotKeys.WALL_FILLING),
    // The filling of the outer edges of the plot (including top) from the plot floor (inclusive)
    OUTLINE(PlotKeys.OUTLINE),
    // The size of the plot, excluding plot borders
    SIZE(PlotKeys.SIZE),
    // The name of the plot
    ALIAS(PlotKeys.ALIAS);

    private final Key<Plot, ?> key;

    PlotProperties(Key<Plot, ?> key) {
        this.key = key;
    }

    public Key<Plot, ?> getKey() {
        return key;
    }
}
