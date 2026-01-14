/*
 * Copyright 2019-2026 the original author or authors.
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

import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.proxy.ProxyOrchestrator;
import org.dockbox.hartshorn.test.annotations.TestComponents;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.dockbox.hartshorn.util.introspect.scan.TypeReferenceCollector;
import org.dockbox.hartshorn.util.introspect.scan.TypeReferenceCollectorContext;
import org.dockbox.hartshorn.util.introspect.scan.classpath.ClasspathTypeReferenceCollector;
import org.junit.jupiter.api.Test;

import test.org.dockbox.hartshorn.launchpad.launch.scanning.discover.ConcreteDiscoverableComponent;
import test.org.dockbox.hartshorn.launchpad.launch.scanning.discover.DiscoverableComponentConfiguration;
import test.org.dockbox.hartshorn.launchpad.launch.scanning.discover.DiscoverableComponent;
import test.org.dockbox.hartshorn.launchpad.launch.scanning.discover.ComponentInterface;
import test.org.dockbox.hartshorn.launchpad.launch.scanning.discover.PackageScanningActivator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@PackageScanningActivator
@HartshornIntegrationTest(includeBasePackages = false)
class ActivatorScanningTests {

    @Test
    void prefixFromActivatorIsRegistered(@Inject TypeReferenceCollectorContext context) {
        for (TypeReferenceCollector collector : context.collectors()) {
            if (collector instanceof ClasspathTypeReferenceCollector referenceCollector
                && referenceCollector.packageNames().contains(PackageScanningActivator.PACKAGE)) {
                return;
            }
        }
        fail("No collector found for package %s".formatted(PackageScanningActivator.PACKAGE));
    }

    @Test
    @TestComponents(DiscoverableComponentConfiguration.class)
    void bindingsFromActivatorPrefixArePresent(@Inject DiscoverableComponent component) {
        assertThat(component).isNotNull();
        assertThat(component.message()).isEqualTo("Demo");
        assertThat(component).isInstanceOf(ConcreteDiscoverableComponent.class);
    }

    @Test
    @TestComponents(ComponentInterface.class)
    void servicesFromActivatorPrefixArePresent(
        @Inject ComponentInterface service,
        @Inject ProxyOrchestrator proxyOrchestrator
    ) {
        assertThat(service).isNotNull();
        assertThat(proxyOrchestrator.isProxy(service)).isTrue();
    }
}
