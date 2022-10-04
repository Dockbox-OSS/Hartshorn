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

package com.nonregistered;

import org.dockbox.hartshorn.application.ApplicationBuilder;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.testsuite.HartshornFactory;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.junit.jupiter.api.Assertions;

@HartshornTest
public class NonRegisteredComponentTests {

    @HartshornFactory
    public static ApplicationBuilder<?, ?> factory(final ApplicationBuilder<?, ?> factory) {
        return factory
                .includeBasePackages(false) // Exclude component scanning in the current package
                .componentLocator(ThrowingComponentLocatorImpl::new);
    }

    @InjectTest
    void testComponents(final ApplicationContext applicationContext) {
        final ApplicationException exception = Assertions.assertThrows(ApplicationException.class, () -> applicationContext.get(DemoComponent.class));
        Assertions.assertEquals("Component key 'com.nonregistered.DemoComponent' is annotated with @Component, but is not registered.", exception.getMessage());
    }
}
