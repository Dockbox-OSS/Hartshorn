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

import org.dockbox.hartshorn.core.domain.tuple.Tuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map.Entry;

public class TupleTests {

    @Test
    public void testFirstValueIsStored() {
        final Entry<?, ?> tuple = new Tuple<>(1, "two");
        Assertions.assertEquals(1, tuple.getKey());
        Assertions.assertTrue(tuple.getKey() instanceof Integer);
    }

    @Test
    public void testSecondValueIsStored() {
        final Entry<?, ?> tuple = new Tuple<>(1, "two");
        Assertions.assertEquals("two", tuple.getValue());
        Assertions.assertTrue(tuple.getValue() instanceof String);
    }

    @Test
    public void testEqualsUsesValues() {
        final Entry<?, ?> tuple = new Tuple<>(1, "two");
        final Entry<?, ?> second = new Tuple<>(1, "two");
        Assertions.assertNotSame(tuple, second);
        Assertions.assertEquals(tuple, second);
    }

}
