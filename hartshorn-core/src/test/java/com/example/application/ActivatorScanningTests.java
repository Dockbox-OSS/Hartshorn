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
        final DemoService demoService = applicationContext.get(DemoService.class);
        Assertions.assertNotNull(demoService);
        Assertions.assertTrue(applicationContext.environment().manager().isProxy(demoService));
    }
}
