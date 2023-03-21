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

package test.org.dockbox.hartshorn.proxy;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.inject.processing.UseServiceProvision;
import org.dockbox.hartshorn.proxy.DefaultValueResponseMethodStub;
import org.dockbox.hartshorn.proxy.MethodStub;
import org.dockbox.hartshorn.proxy.ProxyFactory;
import org.dockbox.hartshorn.proxy.ProxyManager;
import org.dockbox.hartshorn.component.UseProxying;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.ApplicationRuntimeException;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import jakarta.inject.Inject;
import test.org.dockbox.hartshorn.proxy.types.StubbedInterfaceProxy;

@UseServiceProvision
@UseProxying
@HartshornTest(includeBasePackages = false)
public class MethodStubTests {

    @Inject
    private ApplicationContext applicationContext;

    public static Stream<Arguments> factories() {
        return ProxyTests.factories();
    }

    @ParameterizedTest
    @MethodSource("factories")
    void testDefaultBehaviorIsDefaultOrNull(final FactoryFunction<StubbedInterfaceProxy> factory) throws ApplicationException {
        final ProxyFactory<StubbedInterfaceProxy> proxyFactory = factory.apply(StubbedInterfaceProxy.class, this.applicationContext);
        final StubbedInterfaceProxy proxy = proxyFactory.proxy().get();

        final Option<ProxyManager<StubbedInterfaceProxy>> manager = this.applicationContext.environment().manager(proxy);
        Assertions.assertTrue(manager.present());

        final MethodStub<StubbedInterfaceProxy> methodStub = manager.get().stub();
        Assertions.assertTrue(methodStub instanceof DefaultValueResponseMethodStub<StubbedInterfaceProxy>);

        final String stringValue = Assertions.assertDoesNotThrow(proxy::stringTest);
        Assertions.assertNull(stringValue);

        final int intValue = Assertions.assertDoesNotThrow(proxy::integerTest);
        Assertions.assertEquals(0, intValue);
    }

    @ParameterizedTest
    @MethodSource("factories")
    void testStubBehaviorCanBeChanged(final FactoryFunction<StubbedInterfaceProxy> factory) throws ApplicationException {
        final ProxyFactory<StubbedInterfaceProxy> proxyFactory = factory.apply(StubbedInterfaceProxy.class, this.applicationContext);

        // Also verifies that the stub result is not cached
        final AtomicInteger integer = new AtomicInteger(0);
        proxyFactory.defaultStub(context -> integer.incrementAndGet());

        final StubbedInterfaceProxy proxy = proxyFactory.proxy().get();

        final int one = Assertions.<Integer>assertDoesNotThrow(proxy::integerTest);
        Assertions.assertEquals(1, one);

        final int two = Assertions.<Integer>assertDoesNotThrow(proxy::integerTest);
        Assertions.assertEquals(2, two);
    }

    @ParameterizedTest
    @MethodSource("factories")
    void testStubsAreObserved(final FactoryFunction<StubbedInterfaceProxy> factory) throws ApplicationException, NoSuchMethodException {
        final ProxyFactory<StubbedInterfaceProxy> proxyFactory = factory.apply(StubbedInterfaceProxy.class, this.applicationContext);

        final AtomicBoolean beforeObserved = new AtomicBoolean(false);
        final AtomicBoolean afterObserved = new AtomicBoolean(false);
        final AtomicBoolean errorObserved = new AtomicBoolean(false);

        final AtomicBoolean shouldThrow = new AtomicBoolean(false);

        final Method stringTest = StubbedInterfaceProxy.class.getDeclaredMethod("stringTest");
        proxyFactory.intercept(stringTest, wrapper -> wrapper
                .before(context -> beforeObserved.set(true))
                .after(context -> afterObserved.set(true))
                .onError(context -> errorObserved.set(true))
                )
                .defaultStub(context -> {
                    // RuntimeException, as it is not declared in the interface
                    if (shouldThrow.get()) throw new ApplicationRuntimeException("Test");
                    return "test";
                });

        final StubbedInterfaceProxy proxy = proxyFactory.proxy().get();
        Assertions.assertDoesNotThrow(proxy::stringTest);

        Assertions.assertTrue(beforeObserved.get());
        Assertions.assertTrue(afterObserved.get());
        Assertions.assertFalse(errorObserved.get());

        beforeObserved.set(false);
        afterObserved.set(false);
        errorObserved.set(false);
        shouldThrow.set(true);

        Assertions.assertThrows(ApplicationRuntimeException.class, proxy::stringTest);

        Assertions.assertTrue(beforeObserved.get());
        Assertions.assertFalse(afterObserved.get());
        Assertions.assertTrue(errorObserved.get());
    }

    private interface FactoryFunction<T> extends BiFunction<Class<T>, ApplicationContext, ProxyFactory<T>> {
    }
}
