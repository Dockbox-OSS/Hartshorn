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

package test.org.dockbox.hartshorn.prefix;

import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.junit.jupiter.api.Assertions;

/**
 * This test is associated with <a href="https://github.com/GuusLieben/Hartshorn/issues/609">#609</a>. It tests that
 * overly specific packages like {@code com.specific.sub} are not processed twice if a broader package like
 * {@code com.specific} is bound to the same application context.
 */
@Demo
@HartshornTest(includeBasePackages = false, scanPackages = "test.org.dockbox.hartshorn.prefix")
public class SpecificPackageTests {

    @InjectTest
    public void specificPackageFilterIsApplied(ApplicationContext applicationContext) {
        Assertions.assertNotNull(applicationContext);
        DemoServicePreProcessor processor = applicationContext.get(DemoServicePreProcessor.class);
        Assertions.assertEquals(1, processor.processed());
    }
}
