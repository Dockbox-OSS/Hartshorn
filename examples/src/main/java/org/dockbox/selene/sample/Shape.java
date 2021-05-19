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

package org.dockbox.selene.sample;

import org.dockbox.selene.commands.annotations.Parameter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Parameter("shape")
@AllArgsConstructor
@Getter
@ToString
public class Shape {

    private final String name;
    private final int sides;

    private Shape() {
        this.name = "circle";
        this.sides = 1;
    }

    public Shape(int sides) {
        this.name = sides + "-sided shape";
        this.sides = sides;
    }
}
