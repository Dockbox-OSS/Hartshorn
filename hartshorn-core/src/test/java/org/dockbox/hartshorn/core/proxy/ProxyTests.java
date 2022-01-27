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

package org.dockbox.hartshorn.core.proxy;

import org.dockbox.hartshorn.core.annotations.activate.UseProxying;
import org.dockbox.hartshorn.core.annotations.activate.UseServiceProvision;
import org.dockbox.hartshorn.core.annotations.stereotype.Service;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.core.proxy.javassist.JavassistProxyHandler;
import org.dockbox.hartshorn.core.proxy.types.ConcreteProxyTarget;
import org.dockbox.hartshorn.core.proxy.types.FinalProxyTarget;
import org.dockbox.hartshorn.core.proxy.types.ProviderService;
import org.dockbox.hartshorn.core.proxy.types.SampleType;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import lombok.Getter;

@UseServiceProvision
@UseProxying
@HartshornTest
public class ProxyTests {

    @Inject
    @Getter
    private ApplicationContext applicationContext;

    @Test
    void testConcreteMethodsCanBeProxied() throws ApplicationException, NoSuchMethodException {
        final MethodProxyContext<ConcreteProxyTarget, String> property = MethodProxyContext.of(
                ConcreteProxyTarget.class,
                ConcreteProxyTarget.class.getMethod("name"),
                (instance, args, proxyContext) -> "Hartshorn");
        final ProxyHandler<ConcreteProxyTarget> handler = new JavassistProxyHandler<>(this.applicationContext(), new ConcreteProxyTarget());
        handler.delegate(property);
        final ConcreteProxyTarget proxy = handler.proxy();

        Assertions.assertNotNull(proxy);
        Assertions.assertNotNull(proxy.name());
        Assertions.assertEquals("Hartshorn", proxy.name());
    }

    @Test
    void testFinalMethodsCanNotBeProxied() throws ApplicationException, NoSuchMethodException {
        final MethodProxyContext<FinalProxyTarget, String> property = MethodProxyContext.of(
                FinalProxyTarget.class,
                FinalProxyTarget.class.getMethod("name"),
                (instance, args, proxyContext) -> "Hartshorn");
        final ProxyHandler<FinalProxyTarget> handler = new JavassistProxyHandler<>(this.applicationContext(), new FinalProxyTarget());
        Assertions.assertThrows(IllegalArgumentException.class, () -> handler.delegate(property));

        // Ensure the exception isn't thrown after registration
        final FinalProxyTarget proxy = handler.proxy();

        Assertions.assertNotNull(proxy);
        Assertions.assertNotNull(proxy.name());
        Assertions.assertNotEquals("Hartshorn", proxy.name());
        Assertions.assertEquals("NotHartshorn", proxy.name());
    }

    @Test
    void testProxyCanModifyMethods() throws NoSuchMethodException, ApplicationException {
        final MethodProxyContext<ConcreteProxyTarget, String> methodProxyContext = MethodProxyContext.of(
                ConcreteProxyTarget.class,
                ConcreteProxyTarget.class.getMethod("name"),
                (instance, args, proxyContext) -> "Hartshorn");
        final ConcreteProxyTarget concrete = this.applicationContext().get(ConcreteProxyTarget.class);
        final ProxyHandler<ConcreteProxyTarget> handler = this.applicationContext().environment().manager().handler(TypeContext.of(ConcreteProxyTarget.class), concrete);
        handler.delegate(methodProxyContext);
        final ConcreteProxyTarget proxy = handler.proxy();

        Assertions.assertNotNull(proxy);
        Assertions.assertNotNull(proxy.name());
        Assertions.assertEquals("Hartshorn", proxy.name());
    }

    @Test
    void testProxyIsStoredInHandler() throws ApplicationException {
        final ConcreteProxyTarget concrete = this.applicationContext().get(ConcreteProxyTarget.class);
        final ProxyHandler<ConcreteProxyTarget> handler = this.applicationContext().environment().manager().handler(TypeContext.of(ConcreteProxyTarget.class), concrete);
        Assertions.assertTrue(handler.proxyInstance().absent());
        handler.proxy();
        Assertions.assertTrue(handler.proxyInstance().present());
    }

    @Test
    void testProviderService() {
        final ProviderService service = this.applicationContext().get(ProviderService.class);
        final SampleType type = service.get();
        Assertions.assertNotNull(type);
    }

    @InjectTest
    public void proxyEqualityTest(final ApplicationContext applicationContext) {
        final DemoServiceA serviceA1 = applicationContext.get(DemoServiceA.class);
        final DemoServiceA serviceA2 = applicationContext.get(DemoServiceA.class);

        Assertions.assertEquals(serviceA1, serviceA2);

        final DemoServiceB serviceB1 = applicationContext.get(DemoServiceB.class);
        final DemoServiceB serviceB2 = applicationContext.get(DemoServiceB.class);

        Assertions.assertEquals(serviceB1, serviceB2);

        final DemoServiceC serviceC1 = applicationContext.get(DemoServiceC.class);
        final DemoServiceC serviceC2 = applicationContext.get(DemoServiceC.class);

        Assertions.assertEquals(serviceC1, serviceC2);
    }

    @Service
    public static interface DemoServiceA { }

    @Service
    public static class DemoServiceB { }

    @Service
    public static abstract class DemoServiceC { }
}
