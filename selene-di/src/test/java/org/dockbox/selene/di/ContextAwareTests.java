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

package org.dockbox.selene.di;

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.di.context.ApplicationContext;
import org.dockbox.selene.di.types.SampleContextAwareType;
import org.dockbox.selene.test.SeleneJUnit5Runner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SeleneJUnit5Runner.class)
public class ContextAwareTests {

    @Test
    void testApplicationContextIsBound() {
        final ApplicationContext applicationContext = Selene.context().get(ApplicationContext.class);
        Assertions.assertNotNull(applicationContext);

        final SampleContextAwareType sampleContextAwareType = Selene.context().get(SampleContextAwareType.class);
        Assertions.assertNotNull(sampleContextAwareType);
        Assertions.assertNotNull(sampleContextAwareType.getContext());

        Assertions.assertSame(applicationContext, sampleContextAwareType.getContext());
    }
}
