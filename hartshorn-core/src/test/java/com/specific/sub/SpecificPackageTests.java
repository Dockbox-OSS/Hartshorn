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

package com.specific.sub;

import org.dockbox.hartshorn.core.annotations.activate.Activator;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.junit.jupiter.api.Assertions;

/**
 * This test is associated with <a href="https://github.com/GuusLieben/Hartshorn/issues/609">#609</a>. It tests that
 * overly specific packages like {@code com.specific.sub} are not processed twice if a broader package like
 * {@code com.specific} is bound to the same application context.
 */
@Demo
@HartshornTest
@Activator(scanPackages = "com.specific")
public class SpecificPackageTests {

    @InjectTest
    public void someTest(final ApplicationContext applicationContext) {
        Assertions.assertNotNull(applicationContext);
        final DemoServicePreProcessor processor = applicationContext.get(DemoServicePreProcessor.class);
        Assertions.assertEquals(1, processor.processed());
    }
}
