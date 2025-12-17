/*
 * Copyright 2019-2025 the original author or authors.
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

package test.org.dockbox.hartshorn.proxy.delegate;

import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.context.ApplicationContextCarrier;
import org.dockbox.hartshorn.proxy.Proxy;
import org.dockbox.hartshorn.test.annotations.TestComponents;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

@HartshornIntegrationTest(includeBasePackages = false)
public class ApplicationContextCarrierDelegationTests {

    @Test
    @TestComponents(ContextCarrierComponent.class)
    void testContextCarrierDelegation(@Inject ContextCarrierComponent component) throws NoSuchMethodException {
        Option<ApplicationContextCarrier> delegate = findTypeDelegate(component);
        Assertions.assertTrue(delegate.present());

        Option<?> methodDelegate = findMethodDelegate(component);
        Assertions.assertTrue(methodDelegate.absent());

        Assertions.assertNotNull(component.applicationContext());
    }

    @Test
    @TestComponents(OverrideContextCarrierComponentInterface.class)
    void testDefaultCarrierDelegation(@Inject OverrideContextCarrierComponentInterface component) throws NoSuchMethodException {
        Option<ApplicationContextCarrier> delegate = findTypeDelegate(component);
        Assertions.assertTrue(delegate.absent());

        Option<?> methodDelegate = findMethodDelegate(component);
        Assertions.assertTrue(methodDelegate.absent());

        // Default method, should return null (see OverrideContextCarrierComponentInterface)
        Assertions.assertNull(component.applicationContext());
    }

    @Test
    @TestComponents(ContextCarrierComponentInterface.class)
    void testCarrierDelegation(@Inject ContextCarrierComponentInterface component, @Inject ApplicationContext applicationContext) throws NoSuchMethodException {
        Assertions.assertTrue(component instanceof Proxy<?>);
        Method method = ApplicationContextCarrier.class.getMethod("applicationContext");
        Option<?> methodDelegate = ((Proxy<?>) component).manager()
                .advisor()
                .resolver()
                .method(method)
                .delegate();
        Assertions.assertTrue(methodDelegate.present());

        Assertions.assertNotNull(component.applicationContext());
        Assertions.assertSame(component.applicationContext(), applicationContext);
    }

    private static Option<ApplicationContextCarrier> findTypeDelegate(Object object) {
        Proxy<?> proxy = Assertions.assertInstanceOf(Proxy.class, object);
        return proxy.manager()
                .advisor()
                .resolver()
                .type(ApplicationContextCarrier.class)
                .delegate();
    }

    private static Option<?> findMethodDelegate(Object object) throws NoSuchMethodException {
        Proxy<?> proxy = Assertions.assertInstanceOf(Proxy.class, object);
        Method method = ApplicationContextCarrier.class.getMethod("applicationContext");
        return proxy.manager()
                .advisor()
                .resolver()
                .method(method)
                .delegate();
    }
}
