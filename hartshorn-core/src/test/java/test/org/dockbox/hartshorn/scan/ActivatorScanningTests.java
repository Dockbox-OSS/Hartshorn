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

package test.org.dockbox.hartshorn.scan;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.scan.ClasspathTypeReferenceCollector;
import org.dockbox.hartshorn.application.scan.TypeReferenceCollector;
import org.dockbox.hartshorn.application.scan.TypeReferenceCollectorContext;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.dockbox.hartshorn.testsuite.TestComponents;
import org.dockbox.hartshorn.util.Result;
import org.junit.jupiter.api.Assertions;

@UseDemo
@HartshornTest(includeBasePackages = false)
public class ActivatorScanningTests {

    @InjectTest
    void testPrefixFromActivatorIsRegistered(final ApplicationContext applicationContext) {
        final Result<TypeReferenceCollectorContext> contextCandidate = applicationContext.first(TypeReferenceCollectorContext.class);
        Assertions.assertTrue(contextCandidate.present());

        final TypeReferenceCollectorContext context = contextCandidate.get();
        for (final TypeReferenceCollector collector : context.collectors()) {
            if (collector instanceof ClasspathTypeReferenceCollector referenceCollector) {
                if ("test.org.dockbox.hartshorn.scan".equals(referenceCollector.packageName())) {
                    return;
                }
            }
        }
        Assertions.fail("No collector found for package test.org.dockbox.hartshorn.scan");
    }

    @InjectTest
    @TestComponents(DemoProvider.class)
    void testBindingsFromActivatorPrefixArePresent(final ApplicationContext applicationContext) {
        final Demo demo = applicationContext.get(Demo.class);
        Assertions.assertNotNull(demo);
        Assertions.assertEquals("Demo", demo.demo());
        Assertions.assertTrue(demo instanceof DemoImpl);
    }

    @InjectTest
    @TestComponents(DemoService.class)
    void testServicesFromActivatorPrefixArePresent(final ApplicationContext applicationContext) {
        final DemoService demoService = applicationContext.get(DemoService.class);
        Assertions.assertNotNull(demoService);
        Assertions.assertTrue(applicationContext.environment().isProxy(demoService));
    }
}
