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

package org.dockbox.hartshorn.core.boot;

import org.dockbox.hartshorn.core.boot.activators.AbstractActivator;
import org.dockbox.hartshorn.core.boot.activators.InterfaceActivator;
import org.dockbox.hartshorn.core.boot.activators.NonDecoratedActivator;
import org.dockbox.hartshorn.core.boot.activators.ValidActivator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HartshornApplicationTests {

    @Test
    void testCreationFailsWithAbsentDecorator() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> HartshornApplication.create(NonDecoratedActivator.class, new String[0]));
    }

    @Test
    void testCreationFailsWithAbstractActivator() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> HartshornApplication.create(AbstractActivator.class, new String[0]));
    }

    @Test
    void testCreationFailsWithInterfaceActivator() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> HartshornApplication.create(InterfaceActivator.class, new String[0]));
    }

    @Test
    void testCreationSucceedsWithValidActivator() {
        Assertions.assertDoesNotThrow(() -> HartshornApplication.create(ValidActivator.class, new String[0]));
    }
}
