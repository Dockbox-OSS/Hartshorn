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

package org.dockbox.hartshorn.i18n;

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.i18n.annotations.UseTranslations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import lombok.Getter;

@UseTranslations
public class TranslationInjectModifierTests {

    @Inject
    @Getter
    private ApplicationContext applicationContext;

    @Test
    public void testResourceServiceIsProxied() {
        final ITestResources resources = this.applicationContext().get(ITestResources.class);
        Assertions.assertTrue(TypeContext.of(resources).isProxy());
    }

    @Test
    public void testResourceServiceReturnsValidResourceKey() {
        final ITestResources resources = this.applicationContext().get(ITestResources.class);
        final Message testEntry = resources.testEntry();

        Assertions.assertNotNull(testEntry);
        Assertions.assertEquals("resource.test.entry", testEntry.key());
    }

    @Test
    public void testResourceServiceReturnsValidResourceValue() {
        final ITestResources resources = this.applicationContext().get(ITestResources.class);
        final Message testEntry = resources.testEntry();

        Assertions.assertNotNull(testEntry);
        Assertions.assertEquals("Hello world!", testEntry.string());
    }

    @Test
    public void testResourceServiceFormatsParamResource() {
        final ITestResources resources = this.applicationContext().get(ITestResources.class);
        final Message testEntry = resources.parameterTestEntry("world");

        Assertions.assertNotNull(testEntry);
        Assertions.assertEquals("Hello world!", testEntry.string());
    }

    @Test
    void testAbstractServiceAbstractMethodIsProxied() {
        final AbstractTestResources resources = this.applicationContext().get(AbstractTestResources.class);
        final Message testEntry = resources.abstractEntry();

        Assertions.assertNotNull(testEntry);
        Assertions.assertEquals("Hello abstract world!", testEntry.string());
    }

    @Test
    void testAbstractServiceConcreteMethodIsProxied() {
        final AbstractTestResources resources = this.applicationContext().get(AbstractTestResources.class);
        final Message testEntry = resources.concreteEntry();

        Assertions.assertNotNull(testEntry);
        Assertions.assertEquals("Hello concrete world!", testEntry.string());
    }

    @Test
    void testConcreteServiceMethodIsProxied() {
        final TestResources resources = this.applicationContext().get(TestResources.class);
        final Message testEntry = resources.testEntry();

        Assertions.assertNotNull(testEntry);
        Assertions.assertEquals("Hello world!", testEntry.string());
    }
}
