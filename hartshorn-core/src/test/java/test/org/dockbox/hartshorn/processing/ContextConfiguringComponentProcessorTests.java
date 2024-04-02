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

package test.org.dockbox.hartshorn.processing;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.context.ContextKey;
import org.dockbox.hartshorn.proxy.Proxy;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.dockbox.hartshorn.testsuite.ModifyApplication;
import org.dockbox.hartshorn.testsuite.TestComponents;
import org.dockbox.hartshorn.testsuite.TestCustomizer;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;

@HartshornTest(includeBasePackages = false)
public class ContextConfiguringComponentProcessorTests {

    @ModifyApplication
    public static void customize() {
        TestCustomizer.CONSTRUCTOR.compose(constructor -> {
            constructor.componentPostProcessors(processors -> {
                processors.add(new SimpleContextConfiguringComponentProcessor());
            });
        });
    }

    @InjectTest
    @TestComponents(components = EmptyComponent.class)
    void testNonContextComponentIsProcessed(ApplicationContext applicationContext) {
        EmptyComponent emptyComponent = applicationContext.get(EmptyComponent.class);

        Assertions.assertNotNull(emptyComponent);
        Assertions.assertTrue(applicationContext.environment().proxyOrchestrator().isProxy(emptyComponent));

        Proxy<EmptyComponent> component = (Proxy<EmptyComponent>) emptyComponent;
        Option<SimpleContext> context = component.manager().firstContext(ContextKey.of(SimpleContext.class));
        Assertions.assertTrue(context.present());
        Assertions.assertEquals("Foo", context.get().value());
    }

    @InjectTest
    @TestComponents(components = ContextComponent.class)
    void testContextComponentIsProcessed(ApplicationContext applicationContext) {
        ContextComponent contextComponent = applicationContext.get(ContextComponent.class);

        Assertions.assertNotNull(contextComponent);
        Assertions.assertFalse(applicationContext.environment().proxyOrchestrator().isProxy(contextComponent));

        Option<SimpleContext> context = contextComponent.firstContext(ContextKey.of(SimpleContext.class));
        Assertions.assertTrue(context.present());
        Assertions.assertEquals("Foo", context.get().value());
    }
}
