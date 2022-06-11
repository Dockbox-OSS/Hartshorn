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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.context.ContextCarrier;
import org.dockbox.hartshorn.core.types.ContextCarrierService;
import org.dockbox.hartshorn.core.types.IContextCarrierService;
import org.dockbox.hartshorn.core.types.IDefaultContextCarrierService;
import org.dockbox.hartshorn.proxy.Proxy;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.dockbox.hartshorn.testsuite.TestComponents;
import org.dockbox.hartshorn.util.Result;
import org.junit.jupiter.api.Assertions;

import java.lang.reflect.Method;

@HartshornTest
public class ContextCarrierDelegationTests {

    @InjectTest
    @TestComponents(ContextCarrierService.class)
    void testContextCarrierDelegation(final ApplicationContext applicationContext) throws NoSuchMethodException {
        final ContextCarrierService service = applicationContext.get(ContextCarrierService.class);
        this.testDelegateAbsent(service);
        Assertions.assertNotNull(service.applicationContext());
    }

    @InjectTest
    @TestComponents(IDefaultContextCarrierService.class)
    void testDefaultCarrrierDelegation(final ApplicationContext applicationContext) throws NoSuchMethodException {
        final IDefaultContextCarrierService service = applicationContext.get(IDefaultContextCarrierService.class);
        this.testDelegateAbsent(service);
        // Default method, should return null (see IDefaultContextCarrierService)
        Assertions.assertNull(service.applicationContext());
    }

    @InjectTest
    @TestComponents(IContextCarrierService.class)
    void testCarrrierDelegation(final ApplicationContext applicationContext) throws NoSuchMethodException {
        final IContextCarrierService service = applicationContext.get(IContextCarrierService.class);

        Assertions.assertTrue(service instanceof Proxy<?>);
        final Method method = ContextCarrier.class.getMethod("applicationContext");
        final Result<?> methodDelegate = ((Proxy<?>) service).manager().delegate(method);
        Assertions.assertTrue(methodDelegate.present());

        Assertions.assertNotNull(service.applicationContext());
        Assertions.assertSame(service.applicationContext(), applicationContext);
    }
    private void testDelegateAbsent(final Object object) throws NoSuchMethodException {
        if (object instanceof Proxy<?>) {
            final Result<ContextCarrier> delegate = ((Proxy<?>) object).manager().delegate(ContextCarrier.class);
            Assertions.assertTrue(delegate.absent());

            final Method method = ContextCarrier.class.getMethod("applicationContext");
            final Result<?> methodDelegate = ((Proxy<?>) object).manager().delegate(method);
            Assertions.assertTrue(methodDelegate.absent());
        }
    }
}
