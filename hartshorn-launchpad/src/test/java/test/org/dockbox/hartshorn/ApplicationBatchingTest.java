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

package test.org.dockbox.hartshorn;

import org.dockbox.hartshorn.application.HartshornApplication;
import org.dockbox.hartshorn.application.StandardApplicationContextFactory;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.environment.ContextualApplicationEnvironment;
import org.junit.Ignore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import test.org.dockbox.hartshorn.components.SimpleComponent;

@Execution(ExecutionMode.CONCURRENT)
public class ApplicationBatchingTest {

    /**
     * Test that multiple applications can be created and be active at the same time without interfering with each other.
     */
    @Ignore("Only for manual testing")
    @RepeatedTest(100)
    void testApplicationContextBatching() {
        ApplicationContext applicationContext = Assertions.assertDoesNotThrow(() ->
                HartshornApplication.create(ApplicationBatchingTest.class, builder ->
                        builder.applicationContextFactory(StandardApplicationContextFactory.create(constructor -> {
                                    constructor.includeBasePackages(false);
                                    constructor.standaloneComponents(components -> components.add(SimpleComponent.class));
                                    constructor.environment(
                                            ContextualApplicationEnvironment.create(ContextualApplicationEnvironment.Configurer::enableBatchMode)
                                    );
                                })
                        )));
        Assertions.assertNotNull(applicationContext);

        SimpleComponent component = applicationContext.get(SimpleComponent.class);
        Assertions.assertNotNull(component);
        Assertions.assertSame(applicationContext, component.applicationContext());
    }
}
