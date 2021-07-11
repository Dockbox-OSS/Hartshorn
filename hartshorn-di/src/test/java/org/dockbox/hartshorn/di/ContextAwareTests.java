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

package org.dockbox.hartshorn.di;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.MetaProvider;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.services.ComponentLocator;
import org.dockbox.hartshorn.di.types.SampleContextAwareType;
import org.dockbox.hartshorn.test.HartshornRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(HartshornRunner.class)
public class ContextAwareTests {

    @Test
    void testApplicationContextIsBound() {
        final ApplicationContext applicationContext = Hartshorn.context().get(ApplicationContext.class);
        Assertions.assertNotNull(applicationContext);

        final SampleContextAwareType sampleContextAwareType = Hartshorn.context().get(SampleContextAwareType.class);
        Assertions.assertNotNull(sampleContextAwareType);
        Assertions.assertNotNull(sampleContextAwareType.getContext());

        Assertions.assertSame(applicationContext, sampleContextAwareType.getContext());
    }

    @Test
    void testMetaProviderIsBound() {
        final MetaProvider metaProvider = Hartshorn.context().get(MetaProvider.class);
        Assertions.assertNotNull(metaProvider);

        final MetaProvider directMetaProvider = Hartshorn.context().meta();
        Assertions.assertNotNull(directMetaProvider);

        Assertions.assertSame(metaProvider, directMetaProvider);
    }

    @Test
    void testServiceLocatorIsBound() {
        final ComponentLocator componentLocator = Hartshorn.context().get(ComponentLocator.class);
        Assertions.assertNotNull(componentLocator);

        final ComponentLocator directComponentLocator = Hartshorn.context().locator();
        Assertions.assertNotNull(directComponentLocator);

        Assertions.assertSame(componentLocator, directComponentLocator);
    }
}
