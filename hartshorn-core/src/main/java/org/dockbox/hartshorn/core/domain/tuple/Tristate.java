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

package org.dockbox.hartshorn.core.domain.tuple;

/**
 * Represents a simple tristate, which is either <code>true</code>, <code>false</code>, or <code>undefined</code>
 * (indicating the value isn't present, either as a boolean or at all)
 */
public enum Tristate {
    TRUE(true),
    FALSE(false),
    UNDEFINED(false);

    private final boolean booleanValue;

    Tristate(final boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public boolean booleanValue() {
        return this.booleanValue;
    }

    public static Tristate valueOf(final boolean booleanValue) {
        return booleanValue ? TRUE : FALSE;
    }
}
