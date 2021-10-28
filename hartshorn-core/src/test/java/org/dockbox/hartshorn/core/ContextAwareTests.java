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

package org.dockbox.hartshorn.core;

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.services.ComponentLocator;
import test.types.SampleContextAwareType;
import org.dockbox.hartshorn.testsuite.ApplicationAwareTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ContextAwareTests extends ApplicationAwareTest {

    @Test
    void testApplicationContextIsBound() {
        final ApplicationContext applicationContext = this.context().get(ApplicationContext.class);
        Assertions.assertNotNull(applicationContext);

        final SampleContextAwareType sampleContextAwareType = this.context().get(SampleContextAwareType.class);
        Assertions.assertNotNull(sampleContextAwareType);
        Assertions.assertNotNull(sampleContextAwareType.context());

        Assertions.assertSame(applicationContext, sampleContextAwareType.context());
    }

    @Test
    void testMetaProviderIsBound() {
        final MetaProvider metaProvider = this.context().get(MetaProvider.class);
        Assertions.assertNotNull(metaProvider);

        final MetaProvider directMetaProvider = this.context().meta();
        Assertions.assertNotNull(directMetaProvider);

        Assertions.assertSame(metaProvider, directMetaProvider);
    }

    @Test
    void testServiceLocatorIsBound() {
        final ComponentLocator componentLocator = this.context().get(ComponentLocator.class);
        Assertions.assertNotNull(componentLocator);

        final ComponentLocator directComponentLocator = this.context().locator();
        Assertions.assertNotNull(directComponentLocator);

        Assertions.assertSame(componentLocator, directComponentLocator);
    }
}
