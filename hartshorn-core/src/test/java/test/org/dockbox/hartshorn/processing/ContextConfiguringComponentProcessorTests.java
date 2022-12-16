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

package test.org.dockbox.hartshorn.processing;

import org.dockbox.hartshorn.application.ApplicationBuilder;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.context.ContextKey;
import org.dockbox.hartshorn.proxy.Proxy;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.dockbox.hartshorn.testsuite.ModifyApplication;
import org.dockbox.hartshorn.testsuite.TestComponents;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;

@HartshornTest(includeBasePackages = false)
public class ContextConfiguringComponentProcessorTests {

    @ModifyApplication
    public static ApplicationBuilder<?, ?> builder(final ApplicationBuilder<?, ?> builder) {
        return builder.postProcessor(new SimpleContextConfiguringComponentProcessor());
    }

    @InjectTest
    @TestComponents(EmptyComponent.class)
    void testNonContextComponentIsProcessed(final ApplicationContext applicationContext) {
        final EmptyComponent emptyComponent = applicationContext.get(EmptyComponent.class);

        Assertions.assertNotNull(emptyComponent);
        Assertions.assertTrue(applicationContext.environment().isProxy(emptyComponent));

        final Proxy<EmptyComponent> component = (Proxy<EmptyComponent>) emptyComponent;
        final Option<SimpleContext> context = component.manager().first(ContextKey.of(SimpleContext.class));
        Assertions.assertTrue(context.present());
        Assertions.assertEquals("Foo", context.get().value());
    }

    @InjectTest
    @TestComponents(ContextComponent.class)
    void testContextComponentIsProcessed(final ApplicationContext applicationContext) {
        final ContextComponent contextComponent = applicationContext.get(ContextComponent.class);

        Assertions.assertNotNull(contextComponent);
        Assertions.assertFalse(applicationContext.environment().isProxy(contextComponent));

        final Option<SimpleContext> context = contextComponent.first(ContextKey.of(SimpleContext.class));
        Assertions.assertTrue(context.present());
        Assertions.assertEquals("Foo", context.get().value());
    }
}
