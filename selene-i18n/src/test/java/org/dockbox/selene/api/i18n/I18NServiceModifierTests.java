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

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.i18n.common.ResourceEntry;
import org.dockbox.selene.test.SeleneJUnit5Runner;
import org.dockbox.selene.util.Reflect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SeleneJUnit5Runner.class)
public class I18NServiceModifierTests {

    @Test
    public void testResourceServiceIsProxied() {
        ITestResources resources = Selene.context().get(ITestResources.class);
        Assertions.assertTrue(Reflect.isProxy(resources));
    }

    @Test
    public void testResourceServiceReturnsValidResourceKey() {
        ITestResources resources = Selene.context().get(ITestResources.class);
        ResourceEntry testEntry = resources.getTestEntry();

        Assertions.assertNotNull(testEntry);
        Assertions.assertEquals("resource.test.entry", testEntry.getKey());
    }

    @Test
    public void testResourceServiceReturnsValidResourceValue() {
        ITestResources resources = Selene.context().get(ITestResources.class);
        ResourceEntry testEntry = resources.getTestEntry();

        Assertions.assertNotNull(testEntry);
        Assertions.assertEquals("Hello world!", testEntry.plain());
    }

    @Test
    public void testResourceServiceFormatsParamResource() {
        ITestResources resources = Selene.context().get(ITestResources.class);
        ResourceEntry testEntry = resources.getParameterTestEntry("world");

        Assertions.assertNotNull(testEntry);
        Assertions.assertEquals("Hello world!", testEntry.plain());
    }

    @Test
    void testAbstractServiceAbstractMethodIsProxied() {
        AbstractTestResources resources = Selene.context().get(AbstractTestResources.class);
        final ResourceEntry testEntry = resources.getAbstractEntry();

        Assertions.assertNotNull(testEntry);
        Assertions.assertEquals("Hello abstract world!", testEntry.plain());
    }

    @Test
    void testAbstractServiceConcreteMethodIsProxied() {
        AbstractTestResources resources = Selene.context().get(AbstractTestResources.class);
        final ResourceEntry testEntry = resources.getConcreteEntry();

        Assertions.assertNotNull(testEntry);
        Assertions.assertEquals("Hello concrete world!", testEntry.plain());
    }

    @Test
    void testConcreteServiceMethodIsProxied() {
        TestResources resources = Selene.context().get(TestResources.class);
        final ResourceEntry testEntry = resources.getTestEntry();

        Assertions.assertNotNull(testEntry);
        Assertions.assertEquals("Hello world!", testEntry.plain());
    }
}
