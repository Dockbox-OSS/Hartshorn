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

package org.dockbox.hartshorn.config;

import org.dockbox.hartshorn.test.ApplicationAwareTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class ConfigurationManagerTests extends ApplicationAwareTest {

    @Test
    void testNormalValuesAreAccessible() {
        this.context().property("demo", "Hartshorn");
        final ValueTyped typed = this.context().get(ValueTyped.class);

        Assertions.assertNotNull(typed.string());
        Assertions.assertEquals("Hartshorn", typed.string());
    }

    @Test
    void testNestedValuesAreAccessible() {
        final Map<String, Object> values = new HashMap<>();
        values.put("demo", "Hartshorn");
        this.context().property("nested", values);
        final ValueTyped typed = this.context().get(ValueTyped.class);

        Assertions.assertNotNull(typed);
        Assertions.assertNotNull(typed.nestedString());
        Assertions.assertEquals("Hartshorn", typed.nestedString());
    }
}
