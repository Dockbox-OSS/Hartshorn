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

package test.org.dockbox.hartshorn.boot;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;

@HartshornTest(includeBasePackages = false)
public class HartshornTests {

    @InjectTest
    void testLoggersAreReused(final ApplicationContext applicationContext) {
        final Logger l1 = applicationContext.log();
        final Logger l2 = applicationContext.log();

        Assertions.assertNotNull(l1);
        Assertions.assertNotNull(l2);
        Assertions.assertSame(l1, l2);
    }
}