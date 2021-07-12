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

package org.dockbox.hartshorn.api.i18n;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.i18n.common.ResourceEntry;
import org.dockbox.hartshorn.test.HartshornRunner;
import org.dockbox.hartshorn.util.Reflect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(HartshornRunner.class)
public class I18NServiceModifierTests {

    @Test
    public void testResourceServiceIsProxied() {
        ITestResources resources = Hartshorn.context().get(ITestResources.class);
        Assertions.assertTrue(Reflect.isProxy(resources));
    }

    @Test
    public void testResourceServiceReturnsValidResourceKey() {
        ITestResources resources = Hartshorn.context().get(ITestResources.class);
        ResourceEntry testEntry = resources.testEntry();

        Assertions.assertNotNull(testEntry);
        Assertions.assertEquals("resource.test.entry", testEntry.key());
    }

    @Test
    public void testResourceServiceReturnsValidResourceValue() {
        ITestResources resources = Hartshorn.context().get(ITestResources.class);
        ResourceEntry testEntry = resources.testEntry();

        Assertions.assertNotNull(testEntry);
        Assertions.assertEquals("Hello world!", testEntry.plain());
    }

    @Test
    public void testResourceServiceFormatsParamResource() {
        ITestResources resources = Hartshorn.context().get(ITestResources.class);
        ResourceEntry testEntry = resources.parameterTestEntry("world");

        Assertions.assertNotNull(testEntry);
        Assertions.assertEquals("Hello world!", testEntry.plain());
    }

    @Test
    void testAbstractServiceAbstractMethodIsProxied() {
        AbstractTestResources resources = Hartshorn.context().get(AbstractTestResources.class);
        final ResourceEntry testEntry = resources.abstractEntry();

        Assertions.assertNotNull(testEntry);
        Assertions.assertEquals("Hello abstract world!", testEntry.plain());
    }

    @Test
    void testAbstractServiceConcreteMethodIsProxied() {
        AbstractTestResources resources = Hartshorn.context().get(AbstractTestResources.class);
        final ResourceEntry testEntry = resources.concreteEntry();

        Assertions.assertNotNull(testEntry);
        Assertions.assertEquals("Hello concrete world!", testEntry.plain());
    }

    @Test
    void testConcreteServiceMethodIsProxied() {
        TestResources resources = Hartshorn.context().get(TestResources.class);
        final ResourceEntry testEntry = resources.testEntry();

        Assertions.assertNotNull(testEntry);
        Assertions.assertEquals("Hello world!", testEntry.plain());
    }
}
