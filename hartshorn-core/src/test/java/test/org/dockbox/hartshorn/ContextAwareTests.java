/*
 * Copyright 2019-2023 the original author or authors.
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

package test.org.dockbox.hartshorn;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentLocator;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.TestComponents;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import test.org.dockbox.hartshorn.components.SampleContextAwareType;

@HartshornTest(includeBasePackages = false)
public class ContextAwareTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    @TestComponents(components = SampleContextAwareType.class)
    void testApplicationContextIsBound() {
        ApplicationContext applicationContext = this.applicationContext.get(ApplicationContext.class);
        Assertions.assertNotNull(applicationContext);

        SampleContextAwareType sampleContextAwareType = this.applicationContext.get(SampleContextAwareType.class);
        Assertions.assertNotNull(sampleContextAwareType);
        Assertions.assertNotNull(sampleContextAwareType.context());

        Assertions.assertSame(applicationContext, sampleContextAwareType.context());
    }

    @Test
    void testServiceLocatorIsBound() {
        ComponentLocator componentLocator = this.applicationContext.get(ComponentLocator.class);
        Assertions.assertNotNull(componentLocator);
    }
}
