/*
 * Copyright 2019-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package test.org.dockbox.hartshorn.i18n;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.i18n.Message;
import org.dockbox.hartshorn.i18n.annotations.UseTranslations;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.TestComponents;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.dockbox.hartshorn.inject.Inject;

@HartshornTest(includeBasePackages = false)
@UseTranslations
public class TranslationInjectModifierTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    @TestComponents(components = ITestResources.class)
    public void testResourceServiceIsProxied() {
        ITestResources resources = this.applicationContext.get(ITestResources.class);
        boolean proxy = this.applicationContext.environment().proxyOrchestrator().isProxy(resources);
        Assertions.assertTrue(proxy);
    }

    @Test
    @TestComponents(components = ITestResources.class)
    public void testResourceServiceReturnsValidResourceKey() {
        ITestResources resources = this.applicationContext.get(ITestResources.class);
        Message testEntry = resources.testEntry();

        Assertions.assertNotNull(testEntry);
        Assertions.assertEquals("resource.test.entry", testEntry.key());
    }

    @Test
    @TestComponents(components = ITestResources.class)
    public void testResourceServiceReturnsValidResourceValue() {
        ITestResources resources = this.applicationContext.get(ITestResources.class);
        Message testEntry = resources.testEntry();

        Assertions.assertNotNull(testEntry);
        Assertions.assertEquals("Hello world!", testEntry.string());
    }

    @Test
    @TestComponents(components = ITestResources.class)
    public void testResourceServiceFormatsParamResource() {
        ITestResources resources = this.applicationContext.get(ITestResources.class);
        Message testEntry = resources.parameterTestEntry("world");

        Assertions.assertNotNull(testEntry);
        Assertions.assertEquals("Hello world!", testEntry.string());
    }

    @Test
    @TestComponents(components = AbstractTestResources.class)
    void testAbstractServiceAbstractMethodIsProxied() {
        AbstractTestResources resources = this.applicationContext.get(AbstractTestResources.class);
        Message testEntry = resources.abstractEntry();

        Assertions.assertNotNull(testEntry);
        Assertions.assertEquals("Hello abstract world!", testEntry.string());
    }

    @Test
    @TestComponents(components = AbstractTestResources.class)
    void testAbstractServiceConcreteMethodIsProxied() {
        AbstractTestResources resources = this.applicationContext.get(AbstractTestResources.class);
        Message testEntry = resources.concreteEntry();

        Assertions.assertNotNull(testEntry);
        Assertions.assertEquals("Hello concrete world!", testEntry.string());
    }

    @Test
    @TestComponents(components = TestResources.class)
    void testConcreteServiceMethodIsProxied() {
        TestResources resources = this.applicationContext.get(TestResources.class);
        Message testEntry = resources.testEntry();

        Assertions.assertNotNull(testEntry);
        Assertions.assertEquals("Hello world!", testEntry.string());
    }
}
