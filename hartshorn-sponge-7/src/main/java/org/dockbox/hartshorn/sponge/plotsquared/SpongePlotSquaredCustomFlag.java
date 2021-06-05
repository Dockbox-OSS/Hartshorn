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

package org.dockbox.hartshorn.sponge.plotsquared;

import com.intellectualcrafters.plot.flag.Flag;

import org.dockbox.hartshorn.plots.flags.PlotFlag;
import org.dockbox.hartshorn.util.Reflect;

public class SpongePlotSquaredCustomFlag<V> extends Flag<V> {

    private final PlotFlag<V> flag;

    public SpongePlotSquaredCustomFlag(PlotFlag<V> flag) {
        super(flag.getId());
        this.flag = flag;
    }

    @Override
    public String valueToString(Object o) {
        if (o == null) return "null";
        if (Reflect.assignableFrom(this.flag.getType(), o.getClass())) {
            //noinspection unchecked
            return this.flag.serialize((V) o);
        }
        return "INVALID:" + o;
    }

    @Override
    public V parseValue(String s) {
        return this.flag.parse(s);
    }

    @Override
    public String getValueDescription() {
        return this.flag.getDescription().asString();
    }
}
