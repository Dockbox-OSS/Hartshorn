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

package test.org.dockbox.hartshorn.inject.processing;

import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.annotations.TestComponents;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import test.org.dockbox.hartshorn.inject.provider.NonProcessableType;
import test.org.dockbox.hartshorn.inject.provider.NonProcessableTypeProcessor;

@HartshornIntegrationTest(includeBasePackages = false, processors = NonProcessableTypeProcessor.class)
public class ComponentProcessorTests {

    @Test
    @TestComponents(components = NonProcessableType.class)
    void testNonProcessableComponent(@Inject NonProcessableType nonProcessableType) {
        Assertions.assertNotNull(nonProcessableType);
        Assertions.assertNull(nonProcessableType.nonNullIfProcessed());
    }

}
