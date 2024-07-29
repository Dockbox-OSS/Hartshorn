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

package test.org.dockbox.hartshorn.inject.binding.defaults;

import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import test.org.dockbox.hartshorn.launchpad.context.ApplicationContextTests;

@HartshornIntegrationTest(includeBasePackages = false)
public class DefaultBindingsTests {

    @Inject
    private Logger loggerField;

    @Test
    void loggerCanBeInjected(@Inject Logger loggerParameter) {
        Assertions.assertNotNull(loggerParameter);
        // Name should match the consuming class' name, and not the name of the configuration that uses it
        Assertions.assertEquals(loggerParameter.getName(), ApplicationContextTests.class.getName());

        Assertions.assertNotNull(this.loggerField);
        Assertions.assertEquals(this.loggerField.getName(), ApplicationContextTests.class.getName());
    }
}
