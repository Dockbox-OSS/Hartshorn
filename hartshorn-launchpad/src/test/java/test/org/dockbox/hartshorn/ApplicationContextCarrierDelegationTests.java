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

import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.context.ApplicationContextCarrier;
import org.dockbox.hartshorn.proxy.Proxy;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.dockbox.hartshorn.testsuite.TestComponents;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;

import java.lang.reflect.Method;

import test.org.dockbox.hartshorn.components.ContextCarrierService;
import test.org.dockbox.hartshorn.components.IContextCarrierService;
import test.org.dockbox.hartshorn.components.IDefaultContextCarrierService;

@HartshornTest(includeBasePackages = false)
public class ApplicationContextCarrierDelegationTests {

    @InjectTest
    @TestComponents(components = ContextCarrierService.class)
    void testContextCarrierDelegation(ApplicationContext applicationContext) throws NoSuchMethodException {
        ContextCarrierService service = applicationContext.get(ContextCarrierService.class);
        this.testDelegateAbsent(service);
        Assertions.assertNotNull(service.applicationContext());
    }

    @InjectTest
    @TestComponents(components = IDefaultContextCarrierService.class)
    void testDefaultCarrierDelegation(ApplicationContext applicationContext) throws NoSuchMethodException {
        IDefaultContextCarrierService service = applicationContext.get(IDefaultContextCarrierService.class);
        this.testDelegateAbsent(service);
        // Default method, should return null (see IDefaultContextCarrierService)
        Assertions.assertNull(service.applicationContext());
    }

    @InjectTest
    @TestComponents(components = IContextCarrierService.class)
    void testCarrierDelegation(ApplicationContext applicationContext) throws NoSuchMethodException {
        IContextCarrierService service = applicationContext.get(IContextCarrierService.class);

        Assertions.assertTrue(service instanceof Proxy<?>);
        Method method = ApplicationContextCarrier.class.getMethod("applicationContext");
        Option<?> methodDelegate = ((Proxy<?>) service).manager()
                .advisor()
                .resolver()
                .method(method)
                .delegate();
        Assertions.assertTrue(methodDelegate.present());

        Assertions.assertNotNull(service.applicationContext());
        Assertions.assertSame(service.applicationContext(), applicationContext);
    }

    private void testDelegateAbsent(Object object) throws NoSuchMethodException {
        Assertions.assertTrue(object instanceof Proxy<?>);

        Option<ApplicationContextCarrier> delegate = ((Proxy<?>) object).manager()
                .advisor()
                .resolver()
                .type(ApplicationContextCarrier.class)
                .delegate();
        Assertions.assertTrue(delegate.absent());

        Method method = ApplicationContextCarrier.class.getMethod("applicationContext");
        Option<?> methodDelegate = ((Proxy<?>) object).manager()
                .advisor()
                .resolver()
                .method(method)
                .delegate();
        Assertions.assertTrue(methodDelegate.absent());
    }
}
