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

package test.org.dockbox.hartshorn.launchpad.context;

import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@HartshornIntegrationTest(includeBasePackages = false)
public class ApplicationContextTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    void testContextLoads() {
        Assertions.assertNotNull(this.applicationContext);
    }

    // TODO #1003: Restore, types were moved to test fixtures, so need new test components
//    @Test
//    @HartshornTest(includeBasePackages = false, processors = DemoProxyDelegationPostProcessor.class)
//    @TestComponents({AbstractProxy.class, ProxyProviders.class})
//    void testMethodCanDelegateToImplementation() {
//        AbstractProxy abstractProxy = this.applicationContext.get(AbstractProxy.class);
//        Assertions.assertEquals("concrete", abstractProxy.name());
//    }
//
//    @Test
//    @HartshornTest(includeBasePackages = false, processors = DemoProxyDelegationPostProcessor.class)
//    @TestComponents({AbstractProxy.class, ProxyProviders.class})
//    void testMethodOverrideDoesNotDelegateToImplementation() {
//        AbstractProxy abstractProxy = this.applicationContext.get(AbstractProxy.class);
//        Assertions.assertEquals(21, abstractProxy.age());
//    }
}
