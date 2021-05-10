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

package org.dockbox.selene.api;

import org.dockbox.selene.api.domain.tuple.Vector3N;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VectorTests {

    @Test
    void testXCanConvertType() {
        Vector3N vec = Vector3N.of(1, 2, 3);
        Assertions.assertEquals(1, vec.getXi());
        Assertions.assertEquals(1D, vec.getXd());
        Assertions.assertEquals(1F, vec.getXf());
        Assertions.assertEquals(1L, vec.getXl());
    }

    @Test
    void testYCanConvertType() {
        Vector3N vec = Vector3N.of(1, 2, 3);
        Assertions.assertEquals(2, vec.getYi());
        Assertions.assertEquals(2D, vec.getYd());
        Assertions.assertEquals(2F, vec.getYf());
        Assertions.assertEquals(2L, vec.getYl());
    }

    @Test
    void testZCanConvertType() {
        Vector3N vec = Vector3N.of(1, 2, 3);
        Assertions.assertEquals(3, vec.getZi());
        Assertions.assertEquals(3D, vec.getZd());
        Assertions.assertEquals(3F, vec.getZf());
        Assertions.assertEquals(3L, vec.getZl());
    }

    @Test
    void testEqualsUsesValues() {
        Vector3N vec = Vector3N.of(1, 2, 3);
        Vector3N sec = Vector3N.of(1, 2, 3);

        Assertions.assertNotSame(vec, sec);
        Assertions.assertEquals(vec, sec);
    }
}
