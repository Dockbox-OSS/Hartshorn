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
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import lombok.Getter;
import test.types.SampleContextAwareType;

@HartshornTest
public class ContextAwareTests {

    @Inject
    @Getter
    private ApplicationContext applicationContext;

    @Test
    void testApplicationContextIsBound() {
        final ApplicationContext applicationContext = this.applicationContext().get(ApplicationContext.class);
        Assertions.assertNotNull(applicationContext);

        final SampleContextAwareType sampleContextAwareType = this.applicationContext().get(SampleContextAwareType.class);
        Assertions.assertNotNull(sampleContextAwareType);
        Assertions.assertNotNull(sampleContextAwareType.context());

        Assertions.assertSame(applicationContext, sampleContextAwareType.context());
    }

    @Test
    void testMetaProviderIsBound() {
        final MetaProvider metaProvider = this.applicationContext().get(MetaProvider.class);
        Assertions.assertNotNull(metaProvider);

        final MetaProvider directMetaProvider = this.applicationContext().meta();
        Assertions.assertNotNull(directMetaProvider);

        Assertions.assertSame(metaProvider, directMetaProvider);
    }

    @Test
    void testServiceLocatorIsBound() {
        final ComponentLocator componentLocator = this.applicationContext().get(ComponentLocator.class);
        Assertions.assertNotNull(componentLocator);

        final ComponentLocator directComponentLocator = this.applicationContext().locator();
        Assertions.assertNotNull(directComponentLocator);

        Assertions.assertSame(componentLocator, directComponentLocator);
    }
}
