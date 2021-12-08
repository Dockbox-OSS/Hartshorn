/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package com.example.application;

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.junit.jupiter.api.Assertions;

import java.util.Set;

@UseDemo
@HartshornTest
public class ActivatorScanningTests {

    @InjectTest
    void testPrefixFromActivatorIsRegistered(final ApplicationContext applicationContext) {
        final Set<String> prefixes = applicationContext.environment().prefixContext().prefixes();
        Assertions.assertTrue(prefixes.contains("com.example.application"));
    }

    @InjectTest
    void testBindingsFromActivatorPrefixArePresent(final ApplicationContext applicationContext) {
        final Demo demo = applicationContext.get(Demo.class);
        Assertions.assertNotNull(demo);
        Assertions.assertEquals("Demo", demo.demo());
        Assertions.assertTrue(demo instanceof DemoImpl);
    }

    @InjectTest
    void testServicesFromActivatorPrefixArePresent(final ApplicationContext applicationContext) {
        DemoService demoService = applicationContext.get(DemoService.class);
        Assertions.assertNotNull(demoService);
        Assertions.assertTrue(applicationContext.environment().manager().isProxy(demoService));
    }

    @InjectTest
    void testProcessorsFromActivatorPrefixIsPresentAndInjectedWithLocalValues(final ApplicationContext applicationContext) {
        DemoProcessor demoProcessor = applicationContext.get(DemoProcessor.class);
        Assertions.assertNotNull(demoProcessor);
        Assertions.assertTrue(demoProcessor.demo() instanceof DemoImpl);
        Assertions.assertNotNull(demoProcessor.demoService());
        Assertions.assertTrue(applicationContext.environment().manager().isProxy(demoProcessor.demoService()));
    }
}
