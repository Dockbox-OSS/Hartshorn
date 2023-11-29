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

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.dockbox.hartshorn.proxy.ProxyFactory;
import org.dockbox.hartshorn.proxy.ProxyManager;
import org.dockbox.hartshorn.proxy.ProxyOrchestrator;
import org.dockbox.hartshorn.proxy.ProxyOrchestratorLoader;
import org.dockbox.hartshorn.proxy.advice.stub.DefaultValueResponseMethodStub;
import org.dockbox.hartshorn.proxy.advice.stub.MethodStub;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.ApplicationRuntimeException;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import test.org.dockbox.hartshorn.proxy.types.StubbedInterfaceProxy;

public abstract class MethodStubTests {

    protected abstract ProxyOrchestratorLoader orchestratorLoader();

    protected abstract Introspector introspector();

    @Test
    void testDefaultBehaviorIsDefaultOrNull() throws ApplicationException {
        ProxyOrchestrator orchestrator = this.orchestratorLoader().create(this.introspector());
        ProxyFactory<StubbedInterfaceProxy> proxyFactory = orchestrator.factory(StubbedInterfaceProxy.class);
        StubbedInterfaceProxy proxy = proxyFactory.proxy().get();

        Option<ProxyManager<StubbedInterfaceProxy>> manager = orchestrator.manager(proxy);
        Assertions.assertTrue(manager.present());

        MethodStub<StubbedInterfaceProxy> methodStub = manager.get().advisor().resolver().defaultStub().get();
        Assertions.assertTrue(methodStub instanceof DefaultValueResponseMethodStub<StubbedInterfaceProxy>);

        String stringValue = Assertions.assertDoesNotThrow(proxy::stringTest);
        Assertions.assertNull(stringValue);

        int intValue = Assertions.assertDoesNotThrow(proxy::integerTest);
        Assertions.assertEquals(0, intValue);
    }

    @Test
    void testStubBehaviorCanBeChanged() throws ApplicationException {
        ProxyFactory<StubbedInterfaceProxy> proxyFactory = this.orchestratorLoader().create(this.introspector()).factory(StubbedInterfaceProxy.class);

        // Also verifies that the stub result is not cached
        AtomicInteger integer = new AtomicInteger(0);
        proxyFactory.advisors().defaultStub(context -> integer.incrementAndGet());

        StubbedInterfaceProxy proxy = proxyFactory.proxy().get();

        int one = Assertions.<Integer>assertDoesNotThrow(proxy::integerTest);
        Assertions.assertEquals(1, one);

        int two = Assertions.<Integer>assertDoesNotThrow(proxy::integerTest);
        Assertions.assertEquals(2, two);
    }

    @Test
    void testStubsAreObserved() throws ApplicationException, NoSuchMethodException {
        ProxyFactory<StubbedInterfaceProxy> proxyFactory = this.orchestratorLoader().create(this.introspector()).factory(StubbedInterfaceProxy.class);

        AtomicBoolean beforeObserved = new AtomicBoolean(false);
        AtomicBoolean afterObserved = new AtomicBoolean(false);
        AtomicBoolean errorObserved = new AtomicBoolean(false);

        AtomicBoolean shouldThrow = new AtomicBoolean(false);

        Method stringTest = StubbedInterfaceProxy.class.getDeclaredMethod("stringTest");
        proxyFactory.advisors().method(stringTest).wrapAround(wrapper -> wrapper
                .before(context -> beforeObserved.set(true))
                .after(context -> afterObserved.set(true))
                .onError(context -> errorObserved.set(true))
                )
                .defaultStub(context -> {
                    // RuntimeException, as it is not declared in the interface
                    if (shouldThrow.get()) {
                        throw new ApplicationRuntimeException("Test");
                    }
                    return "test";
                });

        StubbedInterfaceProxy proxy = proxyFactory.proxy().get();
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
}
