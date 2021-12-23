/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.core.proxy;

import org.dockbox.hartshorn.core.annotations.activate.UseServiceProvision;
import org.dockbox.hartshorn.core.annotations.proxy.UseProxying;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.core.proxy.javassist.JavassistProxyHandler;
import org.dockbox.hartshorn.core.proxy.types.ConcreteProxyTarget;
import org.dockbox.hartshorn.core.proxy.types.FinalProxyTarget;
import org.dockbox.hartshorn.core.proxy.types.ProviderService;
import org.dockbox.hartshorn.core.proxy.types.SampleType;
import org.dockbox.hartshorn.testsuite.HartshornTest;
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
        final ProxyHandler<ConcreteProxyTarget> handler = new JavassistProxyHandler<>(new ConcreteProxyTarget());
        handler.delegate(property);
        final ConcreteProxyTarget proxy = handler.proxy(this.applicationContext());

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
        final ProxyHandler<FinalProxyTarget> handler = new JavassistProxyHandler<>(new FinalProxyTarget());
        Assertions.assertThrows(ApplicationException.class, () -> handler.delegate(property));

        // Ensure the exception isn't thrown after registration
        final FinalProxyTarget proxy = handler.proxy(this.applicationContext());

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
        final ConcreteProxyTarget proxy = handler.proxy(this.applicationContext());

        Assertions.assertNotNull(proxy);
        Assertions.assertNotNull(proxy.name());
        Assertions.assertEquals("Hartshorn", proxy.name());
    }

    @Test
    void testProxyIsStoredInHandler() throws ApplicationException {
        final ConcreteProxyTarget concrete = this.applicationContext().get(ConcreteProxyTarget.class);
        final ProxyHandler<ConcreteProxyTarget> handler = this.applicationContext().environment().manager().handler(TypeContext.of(ConcreteProxyTarget.class), concrete);
        Assertions.assertTrue(handler.proxyInstance().absent());
        handler.proxy(this.applicationContext());
        Assertions.assertTrue(handler.proxyInstance().present());
    }

    @Test
    void testProviderService() {
        final ProviderService service = this.applicationContext().get(ProviderService.class);
        final SampleType type = service.get();
        Assertions.assertNotNull(type);
    }
}
