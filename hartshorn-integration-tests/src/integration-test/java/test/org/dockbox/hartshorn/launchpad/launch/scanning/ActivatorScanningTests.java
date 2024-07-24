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

package test.org.dockbox.hartshorn.launchpad.launch.scanning;

import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.annotations.TestComponents;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.dockbox.hartshorn.util.introspect.scan.TypeReferenceCollector;
import org.dockbox.hartshorn.util.introspect.scan.TypeReferenceCollectorContext;
import org.dockbox.hartshorn.util.introspect.scan.classpath.ClasspathTypeReferenceCollector;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import test.org.dockbox.hartshorn.launchpad.launch.scanning.discover.ConcreteDiscoverableComponent;
import test.org.dockbox.hartshorn.launchpad.launch.scanning.discover.DiscoverableComponentConfiguration;
import test.org.dockbox.hartshorn.launchpad.launch.scanning.discover.DiscoverableComponent;
import test.org.dockbox.hartshorn.launchpad.launch.scanning.discover.ServiceInterface;
import test.org.dockbox.hartshorn.launchpad.launch.scanning.discover.UseDemo;

@UseDemo
@HartshornIntegrationTest(includeBasePackages = false)
public class ActivatorScanningTests {

    @Test
    void testPrefixFromActivatorIsRegistered(ApplicationContext applicationContext) {
        Option<TypeReferenceCollectorContext> contextCandidate = applicationContext.firstContext(TypeReferenceCollectorContext.class);
        Assertions.assertTrue(contextCandidate.present());

        TypeReferenceCollectorContext context = contextCandidate.get();
        for (TypeReferenceCollector collector : context.collectors()) {
            if (collector instanceof ClasspathTypeReferenceCollector referenceCollector) {
                if ("test.org.dockbox.hartshorn.launchpad.launch.scanning.discover".equals(referenceCollector.packageName())) {
                    return;
                }
            }
        }
        Assertions.fail("No collector found for package test.org.dockbox.hartshorn.launchpad.launch.scanning.discover");
    }

    @Test
    @TestComponents(components = DiscoverableComponentConfiguration.class)
    void testBindingsFromActivatorPrefixArePresent(ApplicationContext applicationContext) {
        DiscoverableComponent component = applicationContext.get(DiscoverableComponent.class);
        Assertions.assertNotNull(component);
        Assertions.assertEquals("Demo", component.message());
        Assertions.assertTrue(component instanceof ConcreteDiscoverableComponent);
    }

    @Test
    @TestComponents(components = ServiceInterface.class)
    void testServicesFromActivatorPrefixArePresent(ApplicationContext applicationContext) {
        ServiceInterface service = applicationContext.get(ServiceInterface.class);
        Assertions.assertNotNull(service);
        Assertions.assertTrue(applicationContext.environment().proxyOrchestrator().isProxy(service));
    }
}
