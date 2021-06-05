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

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.test.HartshornRunner;
import org.dockbox.hartshorn.test.util.JUnitConfigurationManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(HartshornRunner.class)
public class ConfigurationManagerTests {

    @BeforeEach
    void reset() {
        JUnitConfigurationManager.reset();
    }

    @Test
    void testNormalValuesAreAccessible() {
        JUnitConfigurationManager.add("demo", "Hartshorn");
        ValueTyped typed = Hartshorn.context().get(ValueTyped.class);

        Assertions.assertNotNull(typed.getString());
        Assertions.assertEquals("Hartshorn", typed.getString());
    }

    @Test
    void testNestedValuesAreAccessible() {
        Map<String, Object> values = new HashMap<>();
        values.put("demo", "Hartshorn");
        JUnitConfigurationManager.add("nested", values);
        ValueTyped typed = Hartshorn.context().get(ValueTyped.class);

        Assertions.assertNotNull(typed);
        Assertions.assertNotNull(typed.getNestedString());
        Assertions.assertEquals("Hartshorn", typed.getNestedString());
    }
}