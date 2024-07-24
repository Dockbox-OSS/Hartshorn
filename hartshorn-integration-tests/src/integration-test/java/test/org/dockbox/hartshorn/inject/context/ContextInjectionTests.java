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

package test.org.dockbox.hartshorn.inject.context;

import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.inject.populate.ComponentPopulator;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@HartshornIntegrationTest(includeBasePackages = false)
public class ContextInjectionTests {

    @Inject
    private ComponentPopulator componentPopulator;

    @Inject
    private ApplicationContext applicationContext;

    @Test
    void testContextFieldsAreInjected() {
        String contextName = "InjectedContext";
        this.applicationContext.addContext(new SampleContext(contextName));

        ContextInjectedType instance = this.componentPopulator.populate(new ContextInjectedType());

        Assertions.assertNotNull(instance.context());
        Assertions.assertEquals(contextName, instance.context().name());
    }

    @Test
    void testNamedContextFieldsAreInjected() {
        String contextName = "InjectedContext";
        this.applicationContext.addContext("another", new SampleContext(contextName));

        ContextInjectedType instance = this.componentPopulator.populate(new ContextInjectedType());

        Assertions.assertNotNull(instance.anotherContext());
        Assertions.assertEquals(contextName, instance.anotherContext().name());
    }
}
