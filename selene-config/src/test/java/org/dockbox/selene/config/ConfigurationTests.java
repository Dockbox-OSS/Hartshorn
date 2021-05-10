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

package org.dockbox.selene.config;

import org.dockbox.selene.di.Provider;
import org.dockbox.selene.test.SeleneJUnit5Runner;
import org.dockbox.selene.test.util.JUnitConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(SeleneJUnit5Runner.class)
public class ConfigurationTests {

    @Test
    void testNormalValuesAreAccessible() {
        JUnitConfiguration.add("demo", "Selene");
        ValueTyped typed = Provider.provide(ValueTyped.class);

        Assertions.assertNotNull(typed.getString());
        Assertions.assertEquals("Selene", typed.getString());
    }

    @Test
    void testNestedValuesAreAccessible() {
        Map<String, Object> values = new HashMap<>();
        values.put("demo", "Selene");
        JUnitConfiguration.add("nested", values);
        ValueTyped typed = Provider.provide(ValueTyped.class);

        Assertions.assertNotNull(typed.getNestedString());
        Assertions.assertEquals("Selene", typed.getNestedString());
    }
}
