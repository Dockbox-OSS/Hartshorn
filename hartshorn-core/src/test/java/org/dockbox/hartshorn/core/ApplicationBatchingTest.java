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

package org.dockbox.hartshorn.core;

import org.dockbox.hartshorn.application.Activator;
import org.dockbox.hartshorn.application.HartshornApplication;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.core.types.SimpleComponent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@Activator
public class ApplicationBatchingTest {

    /**
     * Test that multiple applications can be created and be active at the same time without
     * interfering with each other.
     */
    @Test
    void testApplicationContextBatching() throws IOException {
        final int count = 10;
        final ApplicationContext[] contexts = new ApplicationContext[count];

        for (int i = 0; i < count; i++) {
            final ApplicationContext applicationContext = Assertions.assertDoesNotThrow(() -> HartshornApplication.create(ApplicationBatchingTest.class));
            Assertions.assertNotNull(applicationContext);
            contexts[i] = applicationContext;
        }

        for (final ApplicationContext context : contexts) {
            final SimpleComponent component = context.get(SimpleComponent.class);
            Assertions.assertNotNull(component);
            Assertions.assertSame(context, component.applicationContext());
        }

        for (final ApplicationContext context : contexts) {
            context.close();
        }
    }
}
