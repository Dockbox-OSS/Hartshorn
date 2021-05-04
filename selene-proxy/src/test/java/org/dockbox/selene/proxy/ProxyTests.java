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

package org.dockbox.selene.proxy;

import org.dockbox.selene.di.Provider;
import org.dockbox.selene.proxy.handle.ProxyHandler;
import org.dockbox.selene.proxy.types.ConcreteProxyTarget;
import org.dockbox.selene.proxy.types.FinalProxyTarget;
import org.dockbox.selene.proxy.types.GlobalProxyTarget;
import org.dockbox.selene.test.SeleneJUnit5Runner;
import org.dockbox.selene.util.Reflect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.reflect.InvocationTargetException;

@ExtendWith(SeleneJUnit5Runner.class)
public class ProxyTests {

    @Test
    void testConcreteMethodsCanBeProxied() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ProxyProperty<ConcreteProxyTarget, String> property = ProxyProperty.of(
                ConcreteProxyTarget.class,
                ConcreteProxyTarget.class.getMethod("getName"),
                (instance, args, holder) -> "Selene");
        ProxyHandler<ConcreteProxyTarget> handler = new ProxyHandler<>(new ConcreteProxyTarget());
        handler.delegate(property);
        ConcreteProxyTarget proxy = handler.proxy();

        Assertions.assertNotNull(proxy);
        Assertions.assertNotNull(proxy.getName());
        Assertions.assertEquals("Selene", proxy.getName());
    }

    @Test
    void testFinalMethodsCanNotBeProxied() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ProxyProperty<FinalProxyTarget, String> property = ProxyProperty.of(
                FinalProxyTarget.class,
                FinalProxyTarget.class.getMethod("getName"),
                (instance, args, holder) -> "Selene");
        ProxyHandler<FinalProxyTarget> handler = new ProxyHandler<>(new FinalProxyTarget());
        handler.delegate(property);
        FinalProxyTarget proxy = handler.proxy();

        Assertions.assertNotNull(proxy);
        Assertions.assertNotNull(proxy.getName());
        Assertions.assertNotEquals("Selene", proxy.getName());
        Assertions.assertEquals("NotSelene", proxy.getName());
    }

    @Test
    void testProviderPropertiesAreApplied() throws NoSuchMethodException {
        ProxyProperty<ConcreteProxyTarget, String> property = ProxyProperty.of(
                ConcreteProxyTarget.class,
                ConcreteProxyTarget.class.getMethod("getName"),
                (instance, args, holder) -> "Selene");
        ConcreteProxyTarget proxy = Provider.provide(ConcreteProxyTarget.class, property);

        Assertions.assertNotNull(proxy);
        Assertions.assertNotNull(proxy.getName());
        Assertions.assertEquals("Selene", proxy.getName());
    }

    @Test
    void testGlobalProxiesCanApply() {
        GlobalProxyTarget target = Provider.provide(GlobalProxyTarget.class);
        Assertions.assertTrue(Reflect.isProxy(target));
        Assertions.assertEquals("GlobalSelene", target.getName());
    }

}
