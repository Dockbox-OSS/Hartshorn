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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

public class SeleneTests {

    @Test
    void testLogReturnsCorrectFormat() {
        final Logger log = Selene.log();
        final String name = log.getName();
        Assertions.assertEquals(35, name.length());
        Assertions.assertEquals("o.d.s.a.SeleneTests", name.trim());
    }

    @Test
    void testLoggersAreReused() {
        final Logger l1 = Selene.log();
        final Logger l2 = Selene.log();

        Assertions.assertNotNull(l1);
        Assertions.assertNotNull(l2);
        Assertions.assertSame(l1, l2);
    }
}
