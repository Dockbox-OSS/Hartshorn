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

package test.org.dockbox.hartshorn.factory;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.dockbox.hartshorn.testsuite.TestComponents;
import org.junit.jupiter.api.Assertions;

@HartshornTest(includeBasePackages = false)
public class FactoryTests {

    @InjectTest
    @TestComponents({FactoryService.class, FactoryProviders.class})
    void testFactoryRespectsPriorities(final ApplicationContext applicationContext) {
        final FactoryService factoryService = applicationContext.get(FactoryService.class);
        final FactoryProvided provided = factoryService.provide("");
        Assertions.assertNotNull(provided);
        Assertions.assertTrue(provided instanceof HighPriorityFactoryBound);
    }
}
