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

package org.dockbox.hartshorn.core;

import org.dockbox.hartshorn.core.domain.tuple.Triad;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TriadTests {

    @Test
    public void testFirstValueIsStored() {
        Triad<?, ?, ?> triad = new Triad<>(1, "two", 3L);
        Assertions.assertEquals(1, triad.first());
        Assertions.assertTrue(triad.first() instanceof Integer);
    }

    @Test
    public void testSecondValueIsStored() {
        Triad<?, ?, ?> triad = new Triad<>(1, "two", 3L);
        Assertions.assertEquals("two", triad.second());
        Assertions.assertTrue(triad.second() instanceof String);
    }

    @Test
    public void testThirdValueIsStored() {
        Triad<?, ?, ?> triad = new Triad<>(1, "two", 3L);
        Assertions.assertEquals(3L, triad.third());
        Assertions.assertTrue(triad.third() instanceof Long);
    }

    @Test
    public void testFirstValueCanBeNull() {
        Triad<?, ?, ?> triad = new Triad<>(null, "two", 3L);
        Assertions.assertNull(triad.first());
    }

    @Test
    public void testSecondValueCanBeNull() {
        Triad<?, ?, ?> triad = new Triad<>(1, null, 3L);
        Assertions.assertNull(triad.second());
    }

    @Test
    public void testThirdValueCanBeNull() {
        Triad<?, ?, ?> triad = new Triad<>(1, "two", null);
        Assertions.assertNull(triad.third());
    }

}
