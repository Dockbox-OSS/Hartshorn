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

package org.dockbox.selene.oldplots;

public enum OldPlotWorld {
    OLDFREEPLOTS(51, 7, -55, -57, 66),
    OLDPLOTS1A(21, 6, -25, -27, 66),
    OLDPLOTS1B(43, 7, -47, -49, 66),
    OLDPLOTS1K(1000, 11, -1006, -1008, 4),
    OLDPLOTS2(151, 7, -155, -157, 66),
    OLDPLOTS3(251, 9, -256, -258, 66);

    private final int size;
    private final int road;
    private final int zeroX;
    private final int zeroZ;
    private final int height;

    OldPlotWorld(int size, int road, int zeroX, int zeroZ, int height) {
        this.size = size;
        this.road = road;
        this.zeroX = zeroX;
        this.zeroZ = zeroZ;
        this.height = height;
    }

    public int getHomeX(int plotX) {
        return this.zeroX + (plotX * (this.size + this.road));
    }

    public int getHomeZ(int plotZ) {
        return this.zeroZ + (plotZ * (this.size + this.road));
    }

    public int getHeight() {
        return this.height;
    }
}
