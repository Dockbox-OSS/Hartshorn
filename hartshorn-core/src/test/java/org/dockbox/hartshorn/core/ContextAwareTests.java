/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.core;

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.services.ComponentLocator;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import test.types.SampleContextAwareType;

@HartshornTest
public class ContextAwareTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    void testApplicationContextIsBound() {
        final ApplicationContext applicationContext = this.applicationContext.get(ApplicationContext.class);
        Assertions.assertNotNull(applicationContext);

        final SampleContextAwareType sampleContextAwareType = this.applicationContext.get(SampleContextAwareType.class);
        Assertions.assertNotNull(sampleContextAwareType);
        Assertions.assertNotNull(sampleContextAwareType.context());

        Assertions.assertSame(applicationContext, sampleContextAwareType.context());
    }

    @Test
    void testMetaProviderIsBound() {
        final MetaProvider metaProvider = this.applicationContext.get(MetaProvider.class);
        Assertions.assertNotNull(metaProvider);

        final MetaProvider directMetaProvider = this.applicationContext.meta();
        Assertions.assertNotNull(directMetaProvider);

        Assertions.assertSame(metaProvider, directMetaProvider);
    }

    @Test
    void testServiceLocatorIsBound() {
        final ComponentLocator componentLocator = this.applicationContext.get(ComponentLocator.class);
        Assertions.assertNotNull(componentLocator);

        final ComponentLocator directComponentLocator = this.applicationContext.locator();
        Assertions.assertNotNull(directComponentLocator);

        Assertions.assertSame(componentLocator, directComponentLocator);
    }
}
