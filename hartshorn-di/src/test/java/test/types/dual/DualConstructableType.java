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

package test.types.dual;

import org.dockbox.hartshorn.di.annotations.inject.Bound;

import test.types.SampleInterface;

public class DualConstructableType implements SampleInterface {

    private final String name;

    @Bound
    public DualConstructableType(final String name) {
        this.name = name;
    }

    public DualConstructableType() {
        this.name = "DefaultConstructor";
    }

    @Override
    public String name() {
        return this.name;
    }
}
