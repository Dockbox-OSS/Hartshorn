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

package org.dockbox.selene.api.i18n;

import org.dockbox.selene.api.i18n.common.ResourceEntry;
import org.dockbox.selene.di.Provider;
import org.dockbox.selene.test.SeleneJUnit5Runner;
import org.dockbox.selene.util.Reflect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SeleneJUnit5Runner.class)
public class I18NPreloadTests {

    @Test
    public void testResourceServiceIsProxied() {
        TestResources resources = Provider.provide(TestResources.class);
        Assertions.assertTrue(Reflect.isProxy(resources));
    }

    @Test
    public void testResourceServiceReturnsValidResourceKey() {
        TestResources resources = Provider.provide(TestResources.class);
        ResourceEntry testEntry = resources.getTestEntry();

        Assertions.assertNotNull(testEntry);
        Assertions.assertEquals("resource.test.entry", testEntry.getKey());
    }

    @Test
    public void testResourceServiceReturnsValidResourceValue() {
        TestResources resources = Provider.provide(TestResources.class);
        ResourceEntry testEntry = resources.getTestEntry();

        Assertions.assertNotNull(testEntry);
        Assertions.assertEquals("Hello world!", testEntry.plain());
    }

    @Test
    public void testResourceServiceFormatsParamResource() {
        TestResources resources = Provider.provide(TestResources.class);
        ResourceEntry testEntry = resources.getParameterTestEntry("world");

        Assertions.assertNotNull(testEntry);
        Assertions.assertEquals("Hello world!", testEntry.plain());
    }

}
