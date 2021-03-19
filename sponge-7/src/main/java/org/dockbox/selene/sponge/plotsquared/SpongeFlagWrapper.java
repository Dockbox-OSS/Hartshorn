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

package org.dockbox.selene.sponge.plotsquared;

import com.intellectualcrafters.plot.flag.Flag;

import org.dockbox.selene.api.i18n.entry.FakeResource;
import org.dockbox.selene.plots.flags.AbstractPlotFlag;

import java.lang.reflect.ParameterizedType;

public class SpongeFlagWrapper<T> extends AbstractPlotFlag<T> {

    private final Flag<T> flag;

    public SpongeFlagWrapper(Flag<T> flag) {
        super(flag.getName(), new FakeResource(flag.getValueDescription()));
        this.flag = flag;
    }

    @Override
    public String serialize(T object) {
        return flag.valueToString(object);
    }

    @Override
    public T parse(String raw) {
        return flag.parseValue(raw);
    }

    @Override
    public Class<T> getType() {
        //noinspection unchecked
        return (Class<T>) ((ParameterizedType) flag.getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }
}
