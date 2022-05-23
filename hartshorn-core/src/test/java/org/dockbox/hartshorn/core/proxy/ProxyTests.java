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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.core.proxy.types.ConcreteProxyTarget;
import org.dockbox.hartshorn.core.proxy.types.FinalProxyTarget;
import org.dockbox.hartshorn.core.proxy.types.ProviderService;
import org.dockbox.hartshorn.core.proxy.types.SampleType;
import org.dockbox.hartshorn.inject.processing.UseServiceProvision;
import org.dockbox.hartshorn.proxy.MethodInterceptor;
import org.dockbox.hartshorn.proxy.MethodWrapper;
import org.dockbox.hartshorn.proxy.Proxy;
import org.dockbox.hartshorn.proxy.ProxyFactory;
import org.dockbox.hartshorn.proxy.ProxyManager;
import org.dockbox.hartshorn.proxy.StateAwareProxyFactory;
import org.dockbox.hartshorn.proxy.UseProxying;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.reflect.MethodContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.inject.Inject;

@UseServiceProvision
@UseProxying
@HartshornTest
public class ProxyTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    void testConcreteMethodsCanBeProxied() throws ApplicationException, NoSuchMethodException {
        final Method name = ConcreteProxyTarget.class.getMethod("name");
        final ProxyFactory<ConcreteProxyTarget, ?> handler = this.applicationContext.environment().manager().factory(ConcreteProxyTarget.class);
        handler.intercept(name, context -> "Hartshorn");
        final ConcreteProxyTarget proxy = handler.proxy().get();

        Assertions.assertNotNull(proxy);
        Assertions.assertNotNull(proxy.name());
        Assertions.assertEquals("Hartshorn", proxy.name());
    }

    @Test
    void testFinalMethodsCanNotBeProxied() throws ApplicationException, NoSuchMethodException {
        final Method name = FinalProxyTarget.class.getMethod("name");
        final ProxyFactory<FinalProxyTarget, ?> handler = this.applicationContext.environment().manager().factory(FinalProxyTarget.class);
        handler.intercept(name, context -> "Hartshorn");
        final FinalProxyTarget proxy = handler.proxy().get();

        Assertions.assertNotNull(proxy);
        Assertions.assertNotNull(proxy.name());
        Assertions.assertNotEquals("Hartshorn", proxy.name());
        Assertions.assertEquals("NotHartshorn", proxy.name());
    }

    public static Stream<Arguments> proxyTypes() {
        return Stream.of(
                Arguments.of(InterfaceProxy.class),
                Arguments.of(AbstractProxy.class),
                Arguments.of(ConcreteProxy.class)
        );
    }

    @Test
    void testRecordProxyCannotBeCreated() {
        // Records are final and cannot be proxied
        final ProxyFactory<RecordProxy, ?> handler = this.applicationContext.environment().manager().factory(RecordProxy.class);
        Assertions.assertThrows(ApplicationException.class, handler::proxy);
    }

    @ParameterizedTest
    @MethodSource("proxyTypes")
    void testEmptyProxyCanCreate(final Class<? extends InterfaceProxy> proxyParent) throws ApplicationException {
        final ProxyFactory<InterfaceProxy, ?> handler = (ProxyFactory<InterfaceProxy, ?>) this.applicationContext.environment().manager().factory(proxyParent);
        final InterfaceProxy proxy = handler.proxy().get();
        Assertions.assertNotNull(proxy);
    }

    @ParameterizedTest
    @MethodSource("proxyTypes")
    void testMethodsCanBeDelegatedToOriginalInstance(final Class<? extends InterfaceProxy> proxyType) throws ApplicationException {
        final ProxyFactory<InterfaceProxy, ?> factory = (ProxyFactory<InterfaceProxy, ?>) this.applicationContext.environment().manager().factory(proxyType);
        factory.delegate(new ConcreteProxy());
        final Result<InterfaceProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());

        final InterfaceProxy proxyInstance = proxy.get();
        Assertions.assertEquals("concrete", proxyInstance.name());
    }

    @ParameterizedTest
    @MethodSource("proxyTypes")
    void testMethodsCanBeIntercepted(final Class<? extends InterfaceProxy> proxyType) throws ApplicationException, NoSuchMethodException {
        final ProxyFactory<InterfaceProxy, ?> factory = (ProxyFactory<InterfaceProxy, ?>) this.applicationContext.environment().manager().factory(proxyType);
        factory.intercept(proxyType.getMethod("name"), context -> "Hartshorn");
        final Result<InterfaceProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());

        final InterfaceProxy proxyInstance = proxy.get();
        Assertions.assertEquals("Hartshorn", proxyInstance.name());
    }

    @ParameterizedTest
    @MethodSource("proxyTypes")
    void testMethodsCanBeDelegated(final Class<? extends InterfaceProxy> proxyType) throws ApplicationException, NoSuchMethodException {
        final ProxyFactory<InterfaceProxy, ?> factory = (ProxyFactory<InterfaceProxy, ?>) this.applicationContext.environment().manager().factory(proxyType);
        factory.delegate(proxyType.getMethod("name"), new ConcreteProxy());
        final Result<InterfaceProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());

        final InterfaceProxy proxyInstance = proxy.get();
        Assertions.assertEquals("concrete", proxyInstance.name());
    }

    @Test
    void testTypesCanBeDelegated() throws ApplicationException {
        // Use a custom interface for this type of delegation, as the other proxy types override methods from their parent
        final ProxyFactory<NamedAgedProxy, ?> factory = this.applicationContext.environment().manager().factory(NamedAgedProxy.class);
        factory.delegate(AgedProxy.class, () -> 12);
        factory.delegate(NamedProxy.class, () -> "NamedProxy");
        final Result<NamedAgedProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());

        final NamedAgedProxy proxyInstance = proxy.get();
        Assertions.assertEquals(12, proxyInstance.age());
        Assertions.assertEquals("NamedProxy", proxyInstance.name());
    }

    @Test
    void testProxyWillYieldExceptionOnMissingProperty() throws ApplicationException {
        // Use a custom interface for this type of delegation, as the other proxy types override methods from their parent
        final ProxyFactory<AgedProxy, ?> factory = this.applicationContext.environment().manager().factory(AgedProxy.class);
        final Result<AgedProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());

        final AgedProxy proxyInstance = proxy.get();
        final IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class, proxyInstance::age);
        Assertions.assertTrue(StringUtilities.notEmpty(exception.getMessage()));
    }

    @ParameterizedTest
    @MethodSource("proxyTypes")
    void testWrapperInterceptionIsCorrect(final Class<? extends InterfaceProxy> proxyType) throws NoSuchMethodException, ApplicationException {
        final ProxyFactory<InterfaceProxy, ?> factory = (ProxyFactory<InterfaceProxy, ?>) this.applicationContext.environment().manager().factory(proxyType);
        final AtomicInteger count = new AtomicInteger();
        factory.intercept(proxyType.getMethod("name"), context -> "done");
        factory.intercept(proxyType.getMethod("name"), new MethodWrapper<>() {
            @Override
            public void acceptBefore(final MethodContext<?, InterfaceProxy> method, final InterfaceProxy instance, final Object[] args) {
                Assertions.assertEquals(0, count.getAndIncrement());
            }

            @Override
            public void acceptAfter(final MethodContext<?, InterfaceProxy> method, final InterfaceProxy instance, final Object[] args) {
                Assertions.assertEquals(1, count.getAndIncrement());
            }

            @Override
            public void acceptError(final MethodContext<?, InterfaceProxy> method, final InterfaceProxy instance, final Object[] args, final Throwable error) {
                // Not thrown
                Assertions.fail();
            }
        });
        final Result<InterfaceProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());

        final InterfaceProxy proxyInstance = proxy.get();
        Assertions.assertEquals("done", proxyInstance.name());
        Assertions.assertEquals(2, count.get());
    }

    @ParameterizedTest
    @MethodSource("proxyTypes")
    void testErrorWrapperInterceptionIsCorrect(final Class<? extends InterfaceProxy> proxyType) throws NoSuchMethodException, ApplicationException {
        final ProxyFactory<InterfaceProxy, ?> factory = (ProxyFactory<InterfaceProxy, ?>) this.applicationContext.environment().manager().factory(proxyType);
        final AtomicInteger count = new AtomicInteger();
        factory.intercept(proxyType.getMethod("name"), context -> {
            throw new IllegalStateException("not done");
        });
        factory.intercept(proxyType.getMethod("name"), new MethodWrapper<>() {
            @Override
            public void acceptBefore(final MethodContext<?, InterfaceProxy> method, final InterfaceProxy instance, final Object[] args) {
                Assertions.assertEquals(0, count.getAndIncrement());
            }

            @Override
            public void acceptAfter(final MethodContext<?, InterfaceProxy> method, final InterfaceProxy instance, final Object[] args) {
                Assertions.fail();
            }

            @Override
            public void acceptError(final MethodContext<?, InterfaceProxy> method, final InterfaceProxy instance, final Object[] args, final Throwable error) {
                Assertions.assertNotNull(error);
                Assertions.assertTrue(error instanceof IllegalStateException);
                Assertions.assertEquals("not done", error.getMessage());
                Assertions.assertEquals(1, count.getAndIncrement());
            }
        });
        final Result<InterfaceProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());

        final InterfaceProxy proxyInstance = proxy.get();
        final IllegalStateException error = Assertions.assertThrows(IllegalStateException.class, proxyInstance::name);
        Assertions.assertEquals("not done", error.getMessage());
        Assertions.assertEquals(2, count.get());
    }

    @Test
    void testProxyManagerTracksInterceptorsAndDelegates() throws NoSuchMethodException, ApplicationException {
        final ProxyFactory<NamedAgedProxy, ?> factory = this.applicationContext.environment().manager().factory(NamedAgedProxy.class);

        final AgedProxy aged = () -> 12;
        factory.delegate(AgedProxy.class, aged);

        final MethodInterceptor<NamedAgedProxy> named = context -> "NamedProxy";
        factory.intercept(NamedProxy.class.getMethod("name"), named);
        final Result<NamedAgedProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());

        final Proxy proxyInstance = (Proxy) proxy.get();
        final ProxyManager manager = proxyInstance.manager();

        final Result agedDelegate = manager.delegate(AgedProxy.class);
        Assertions.assertTrue(agedDelegate.present());
        Assertions.assertSame(agedDelegate.get(), aged);

        final Result namedInterceptor = manager.interceptor(NamedProxy.class.getMethod("name"));
        Assertions.assertTrue(namedInterceptor.present());
        Assertions.assertSame(namedInterceptor.get(), named);
    }

    @ParameterizedTest
    @MethodSource("proxyTypes")
    void testProxyCanHaveExtraInterfaces(final Class<? extends InterfaceProxy> proxyType) throws ApplicationException {
        final ProxyFactory<InterfaceProxy, ?> factory = (ProxyFactory<InterfaceProxy, ?>) this.applicationContext.environment().manager().factory(proxyType);
        factory.implement(DescribedProxy.class);
        final Result<InterfaceProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());

        final InterfaceProxy proxyInstance = proxy.get();
        Assertions.assertTrue(proxyInstance instanceof DescribedProxy);
    }


    @ParameterizedTest
    @MethodSource("proxyTypes")
    void testProxiesAlwaysImplementProxyType(final Class<? extends InterfaceProxy> proxyType) throws ApplicationException {
        final ProxyFactory<InterfaceProxy, ?> factory = (ProxyFactory<InterfaceProxy, ?>) this.applicationContext.environment().manager().factory(proxyType);
        final Result<InterfaceProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());
        final InterfaceProxy proxyInstance = proxy.get();
        Assertions.assertTrue(proxyInstance instanceof Proxy);
    }

    @ParameterizedTest
    @MethodSource("proxyTypes")
    void testProxiesExposeManager(final Class<? extends InterfaceProxy> proxyType) throws ApplicationException {
        final ProxyFactory<InterfaceProxy, ?> factory = (ProxyFactory<InterfaceProxy, ?>) this.applicationContext.environment().manager().factory(proxyType);
        final Result<InterfaceProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());

        final Proxy proxyInstance = (Proxy) proxy.get();
        Assertions.assertNotNull(proxyInstance.manager());
    }

    @ParameterizedTest
    @MethodSource("proxyTypes")
    void testProxyManagerExposesTargetAndProxyType(final Class<? extends InterfaceProxy> proxyType) throws ApplicationException {
        final ProxyFactory<InterfaceProxy, ?> factory = (ProxyFactory<InterfaceProxy, ?>) this.applicationContext.environment().manager().factory(proxyType);
        final Result<InterfaceProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());

        final ProxyManager manager = ((Proxy) proxy.get()).manager();
        Assertions.assertNotNull(manager.proxyClass());
        Assertions.assertNotNull(manager.targetClass());

        Assertions.assertNotEquals(proxyType, manager.proxyClass());
        Assertions.assertEquals(proxyType, manager.targetClass());

        Assertions.assertTrue(this.applicationContext.environment().manager().isProxy(manager.proxyClass()));
    }

    @Test
    void testProviderService() {
        final ProviderService service = this.applicationContext.get(ProviderService.class);
        Assertions.assertNotNull(service);
        Assertions.assertTrue(service instanceof Proxy);
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
    public interface DemoServiceA { }

    @Service
    public static class DemoServiceB { }

    @Service
    public abstract static class DemoServiceC { }

    @Test
    void testConcreteProxySelfEquality() throws ApplicationException {
        final ProxyFactory<EqualProxy, ?> factory = this.applicationContext.environment().manager().factory(EqualProxy.class);
        final Result<EqualProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());

        final EqualProxy proxyInstance = proxy.get();
        Assertions.assertEquals(proxyInstance, proxyInstance);
        Assertions.assertTrue(proxyInstance.test(proxyInstance));
    }

    @Test
    void testServiceSelfEquality() {
        final EqualServiceProxy service = this.applicationContext.get(EqualServiceProxy.class);
        Assertions.assertEquals(service, service);
        Assertions.assertTrue(service.test(service));
    }

    @Test
    void testInterfaceProxySelfEquality() throws ApplicationException {
        final ProxyFactory<EqualInterfaceProxy, ?> factory = this.applicationContext.environment().manager().factory(EqualInterfaceProxy.class);
        final Result<EqualInterfaceProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());

        final EqualInterfaceProxy proxyInstance = proxy.get();
        Assertions.assertEquals(proxyInstance, proxyInstance);
        Assertions.assertTrue(proxyInstance.test(proxyInstance));
    }

    @Test
    void testLambdaCanBeProxied() throws NoSuchMethodException, ApplicationException {
        final StateAwareProxyFactory<Supplier, ?> factory = this.applicationContext.environment().manager().factory(Supplier.class);
        factory.intercept(Supplier.class.getMethod("get"), context -> "foo");
        final Result<Supplier> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());
        Assertions.assertEquals("foo", proxy.get().get());
    }
}
