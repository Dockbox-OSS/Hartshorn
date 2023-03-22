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

import org.dockbox.hartshorn.proxy.ApplicationProxier;
import org.dockbox.hartshorn.proxy.ApplicationProxierLoader;
import org.dockbox.hartshorn.proxy.MethodInterceptor;
import org.dockbox.hartshorn.proxy.MethodWrapper;
import org.dockbox.hartshorn.proxy.Proxy;
import org.dockbox.hartshorn.proxy.ProxyCallbackContext;
import org.dockbox.hartshorn.proxy.ProxyFactory;
import org.dockbox.hartshorn.proxy.ProxyManager;
import org.dockbox.hartshorn.proxy.StateAwareProxyFactory;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.function.CheckedSupplier;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Stream;

import test.org.dockbox.hartshorn.proxy.types.ConcreteProxyTarget;
import test.org.dockbox.hartshorn.proxy.types.FinalProxyTarget;

@SuppressWarnings("unchecked")
public abstract class ProxyTests {

    protected abstract ApplicationProxierLoader proxierLoader();
    protected abstract Introspector introspector();

    @Test
    void testConcreteMethodsCanBeProxied() throws ApplicationException, NoSuchMethodException {
        final Method name = ConcreteProxyTarget.class.getMethod("name");
        final ProxyFactory<ConcreteProxyTarget> handler = proxierLoader().create(this.introspector()).factory(ConcreteProxyTarget.class);
        handler.intercept(name, context -> "Hartshorn");
        final ConcreteProxyTarget proxy = handler.proxy().get();

        Assertions.assertNotNull(proxy);
        Assertions.assertNotNull(proxy.name());
        Assertions.assertEquals("Hartshorn", proxy.name());
    }

    @Test
    void testFinalMethodsCanNotBeProxied() throws ApplicationException, NoSuchMethodException {
        final Method name = FinalProxyTarget.class.getMethod("name");
        final ProxyFactory<FinalProxyTarget> handler = proxierLoader().create(this.introspector()).factory(FinalProxyTarget.class);
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
        final ProxyFactory<RecordProxy> handler = proxierLoader().create(this.introspector()).factory(RecordProxy.class);
        Assertions.assertThrows(ApplicationException.class, handler::proxy);
    }

    @ParameterizedTest
    @MethodSource("proxyTypes")
    void testEmptyProxyCanCreate(final Class<? extends InterfaceProxy> proxyParent) throws ApplicationException {
        final ProxyFactory<? extends InterfaceProxy> handler = proxierLoader().create(this.introspector()).factory(proxyParent);
        final InterfaceProxy proxy = handler.proxy().get();
        Assertions.assertNotNull(proxy);
    }

    @ParameterizedTest
    @MethodSource("proxyTypes")
    void testMethodsCanBeDelegatedToOriginalInstance(final Class<InterfaceProxy> proxyType) throws ApplicationException {
        final ProxyFactory<InterfaceProxy> factory = proxierLoader().create(this.introspector()).factory(proxyType);
        factory.delegate(new ConcreteProxy());
        final Option<InterfaceProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());

        final InterfaceProxy proxyInstance = proxy.get();
        Assertions.assertEquals("concrete", proxyInstance.name());
    }

    @Test
    void testConcreteProxyWithNonDefaultConstructorUsesConstructor() {
        final StateAwareProxyFactory<ConcreteProxyWithNonDefaultConstructor> factory = proxierLoader().create(this.introspector()).factory(ConcreteProxyWithNonDefaultConstructor.class);

        final TypeView<ConcreteProxyWithNonDefaultConstructor> typeView = this.introspector().introspect(ConcreteProxyWithNonDefaultConstructor.class);
        final ConstructorView<ConcreteProxyWithNonDefaultConstructor> constructor = typeView.constructors().all().get(0);
        final Option<ConcreteProxyWithNonDefaultConstructor> proxy = Assertions.assertDoesNotThrow(() -> factory.proxy(constructor, new Object[]{"Hello world"}));
        Assertions.assertTrue(proxy.present());

        final ConcreteProxyWithNonDefaultConstructor proxyInstance = proxy.get();
        Assertions.assertEquals("Hello world", proxyInstance.message());
    }

    @ParameterizedTest
    @MethodSource("proxyTypes")
    void testMethodsCanBeIntercepted(final Class<? extends InterfaceProxy> proxyType) throws ApplicationException, NoSuchMethodException {
        final ProxyFactory<? extends InterfaceProxy> factory = proxierLoader().create(this.introspector()).factory(proxyType);
        factory.intercept(proxyType.getMethod("name"), context -> "Hartshorn");
        final Option<? extends InterfaceProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());

        final InterfaceProxy proxyInstance = proxy.get();
        Assertions.assertEquals("Hartshorn", proxyInstance.name());
    }

    @ParameterizedTest
    @MethodSource("proxyTypes")
    void testMethodsCanBeDelegated(final Class<? extends InterfaceProxy> proxyType) throws ApplicationException, NoSuchMethodException {
        final ProxyFactory<InterfaceProxy> factory = (ProxyFactory<InterfaceProxy>) proxierLoader().create(this.introspector()).factory(proxyType);
        factory.delegate(proxyType.getMethod("name"), new ConcreteProxy());
        final Option<? extends InterfaceProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());

        final InterfaceProxy proxyInstance = proxy.get();
        Assertions.assertEquals("concrete", proxyInstance.name());
    }

    @Test
    void testTypesCanBeDelegated() throws ApplicationException {
        // Use a custom interface for this type of delegation, as the other proxy types override methods from their parent
        final ProxyFactory<NamedAgedProxy> factory = proxierLoader().create(this.introspector()).factory(NamedAgedProxy.class);
        factory.delegate(AgedProxy.class, () -> 12);
        factory.delegate(NamedProxy.class, () -> "NamedProxy");
        final Option<NamedAgedProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());

        final NamedAgedProxy proxyInstance = proxy.get();
        Assertions.assertEquals(12, proxyInstance.age());
        Assertions.assertEquals("NamedProxy", proxyInstance.name());
    }

    @ParameterizedTest
    @MethodSource("proxyTypes")
    void testWrapperInterceptionIsCorrect(final Class<? extends InterfaceProxy> proxyType) throws NoSuchMethodException, ApplicationException {
        final ProxyFactory<InterfaceProxy> factory = (ProxyFactory<InterfaceProxy>) proxierLoader().create(this.introspector()).factory(proxyType);
        final AtomicInteger count = new AtomicInteger();
        factory.intercept(proxyType.getMethod("name"), context -> "done");
        factory.wrapAround(proxyType.getMethod("name"), new MethodWrapper<>() {
            @Override
            public void acceptBefore(final ProxyCallbackContext<InterfaceProxy> context) {
                Assertions.assertEquals(0, count.getAndIncrement());
            }

            @Override
            public void acceptAfter(final ProxyCallbackContext<InterfaceProxy> context) {
                Assertions.assertEquals(1, count.getAndIncrement());
            }

            @Override
            public void acceptError(final ProxyCallbackContext<InterfaceProxy> context) {
                // Not thrown
                Assertions.fail();
            }
        });
        final Option<InterfaceProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());

        final InterfaceProxy proxyInstance = proxy.get();
        Assertions.assertEquals("done", proxyInstance.name());
        Assertions.assertEquals(2, count.get());
    }

    @ParameterizedTest
    @MethodSource("proxyTypes")
    void testErrorWrapperInterceptionIsCorrect(final Class<? extends InterfaceProxy> proxyType) throws NoSuchMethodException, ApplicationException {
        final ProxyFactory<InterfaceProxy> factory = (ProxyFactory<InterfaceProxy>) proxierLoader().create(this.introspector()).factory((proxyType));
        final AtomicInteger count = new AtomicInteger();
        factory.intercept(proxyType.getMethod("name"), context -> {
            throw new IllegalStateException("not done");
        });
        factory.wrapAround(proxyType.getMethod("name"), new MethodWrapper<>() {
            @Override
            public void acceptBefore(final ProxyCallbackContext<InterfaceProxy> context) {
                Assertions.assertEquals(0, count.getAndIncrement());
            }

            @Override
            public void acceptAfter(final ProxyCallbackContext<InterfaceProxy> context) {
                Assertions.fail();
            }

            @Override
            public void acceptError(final ProxyCallbackContext<InterfaceProxy> context) {
                final Throwable error = context.error();
                Assertions.assertNotNull(error);
                Assertions.assertTrue(error instanceof IllegalStateException);
                Assertions.assertEquals("not done", error.getMessage());
                Assertions.assertEquals(1, count.getAndIncrement());
            }
        });
        final Option<InterfaceProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());

        final InterfaceProxy proxyInstance = proxy.get();
        final IllegalStateException error = Assertions.assertThrows(IllegalStateException.class, proxyInstance::name);
        Assertions.assertEquals("not done", error.getMessage());
        Assertions.assertEquals(2, count.get());
    }

    @Test
    void testProxyManagerTracksInterceptorsAndDelegates() throws NoSuchMethodException, ApplicationException {
        final ProxyFactory<NamedAgedProxy> factory = proxierLoader().create(this.introspector()).factory(NamedAgedProxy.class);

        final AgedProxy aged = () -> 12;
        factory.delegate(AgedProxy.class, aged);

        final MethodInterceptor<NamedAgedProxy, ?> named = context -> "NamedProxy";
        factory.intercept(NamedProxy.class.getMethod("name"), named);
        final Option<NamedAgedProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());

        final Proxy<?> proxyInstance = (Proxy<?>) proxy.get();
        final ProxyManager<?> manager = proxyInstance.manager();

        final Option<?> agedDelegate = manager.delegate(AgedProxy.class);
        Assertions.assertTrue(agedDelegate.present());
        Assertions.assertSame(agedDelegate.get(), aged);

        final Option<?> namedInterceptor = manager.interceptor(NamedProxy.class.getMethod("name"));
        Assertions.assertTrue(namedInterceptor.present());
        Assertions.assertSame(namedInterceptor.get(), named);
    }

    @ParameterizedTest
    @MethodSource("proxyTypes")
    void testProxyCanHaveExtraInterfaces(final Class<? extends InterfaceProxy> proxyType) throws ApplicationException {
        final ProxyFactory<InterfaceProxy> factory = (ProxyFactory<InterfaceProxy>) proxierLoader().create(this.introspector()).factory(proxyType);
        factory.implement(DescribedProxy.class);
        final Option<InterfaceProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());

        final InterfaceProxy proxyInstance = proxy.get();
        Assertions.assertTrue(proxyInstance instanceof DescribedProxy);
    }


    @ParameterizedTest
    @MethodSource("proxyTypes")
    void testProxiesAlwaysImplementProxyType(final Class<? extends InterfaceProxy> proxyType) throws ApplicationException {
        final ProxyFactory<InterfaceProxy> factory = (ProxyFactory<InterfaceProxy>) proxierLoader().create(this.introspector()).factory(proxyType);
        final Option<InterfaceProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());
        final InterfaceProxy proxyInstance = proxy.get();
        Assertions.assertTrue(proxyInstance instanceof Proxy);
    }

    @ParameterizedTest
    @MethodSource("proxyTypes")
    void testProxiesExposeManager(final Class<? extends InterfaceProxy> proxyType) throws ApplicationException {
        final ProxyFactory<InterfaceProxy> factory = (ProxyFactory<InterfaceProxy>) proxierLoader().create(this.introspector()).factory(proxyType);
        final Option<InterfaceProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());

        final Proxy<?> proxyInstance = (Proxy<?>) proxy.get();
        Assertions.assertNotNull(proxyInstance.manager());
    }

    @ParameterizedTest
    @MethodSource("proxyTypes")
    void testProxyManagerExposesTargetAndProxyType(final Class<? extends InterfaceProxy> proxyType) throws ApplicationException {
        final ProxyFactory<InterfaceProxy> factory = (ProxyFactory<InterfaceProxy>) proxierLoader().create(this.introspector()).factory(proxyType);
        final Option<InterfaceProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());

        final ProxyManager<InterfaceProxy> manager = ((Proxy<InterfaceProxy>) proxy.get()).manager();
        Assertions.assertNotNull(manager.proxyClass());
        Assertions.assertNotNull(manager.targetClass());

        Assertions.assertNotEquals(proxyType, manager.proxyClass());
        Assertions.assertSame(proxyType, manager.targetClass());

        Assertions.assertTrue(manager.applicationProxier().isProxy(manager.proxyClass()));
    }

    @Test
    void testInterfaceProxyDoesNotEqual() throws ApplicationException {
        final DemoServiceA serviceA1 = this.createProxy(DemoServiceA.class);
        final DemoServiceA serviceA2 = this.createProxy(DemoServiceA.class);

        Assertions.assertNotSame(serviceA1, serviceA2);
        Assertions.assertNotEquals(serviceA1, serviceA2);
    }

    @Test
    void testAbstractClassProxyDoesNotEqual() throws ApplicationException {
        final DemoServiceB serviceC1 = this.createProxy(DemoServiceB.class);
        final DemoServiceB serviceC2 = this.createProxy(DemoServiceB.class);

        Assertions.assertNotSame(serviceC1, serviceC2);
        Assertions.assertNotEquals(serviceC1, serviceC2);
    }

    @Test
    void testConcreteClassProxyWithoutDelegateDoesNotEqual() throws ApplicationException {
        final DemoServiceC serviceB1 = this.createProxy(DemoServiceC.class);
        final DemoServiceC serviceB2 = this.createProxy(DemoServiceC.class);

        Assertions.assertNotSame(serviceB1, serviceB2);
        Assertions.assertNotEquals(serviceB1, serviceB2);
    }

    @Test
    public void testConcreteClassProxyWithNonEqualsImplementedDelegateDoesNotEqual() throws ApplicationException {
        CheckedSupplier<DemoServiceC> supplier = () -> proxierLoader().create(this.introspector())
                .factory(DemoServiceC.class)
                .delegate(new DemoServiceC())
                .proxy()
                .get();

        DemoServiceC serviceC3 = supplier.get();
        DemoServiceC serviceC4 = supplier.get();

        Assertions.assertNotSame(serviceC3, serviceC4);
        Assertions.assertNotEquals(serviceC3, serviceC4);
    }

    @Test
    void testConcreteClassProxyWithDelegateDoesNotEqual() throws ApplicationException {
        CheckedSupplier<DemoServiceD> supplier = () -> proxierLoader().create(this.introspector())
                .factory(DemoServiceD.class)
                .delegate(new DemoServiceD("name"))
                .proxy()
                .get();

        final DemoServiceD serviceD1 = supplier.get();
        final DemoServiceD serviceD2 = supplier.get();

        Assertions.assertNotSame(serviceD1, serviceD2);
        Assertions.assertEquals(serviceD1, serviceD2);
    }

    private <T> T createProxy(Class<T> type) throws ApplicationException {
        return proxierLoader().create(this.introspector()).factory(type).proxy().get();
    }

    public interface DemoServiceA { }

    public abstract static class DemoServiceB { }

    public static class DemoServiceC { }

    public static class DemoServiceD {
        private String name;

        public DemoServiceD(String name) {
            this.name = name;
        }

        @SuppressWarnings("unused")
        public DemoServiceD() {
            // Default constructor for proxier. Note that this is typically handled by providing a constructor to the
            // proxier factory, but this is a test, so we're not doing that.
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DemoServiceD that = (DemoServiceD) o;
            return name.equals(that.name);
        }
    }

    @Test
    void testConcreteProxySelfEquality() throws ApplicationException {
        final ProxyFactory<EqualProxy> factory = proxierLoader().create(this.introspector()).factory(EqualProxy.class);
        final Option<EqualProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());

        final EqualProxy proxyInstance = proxy.get();
        Assertions.assertEquals(proxyInstance, proxyInstance);
        Assertions.assertTrue(proxyInstance.test(proxyInstance));
    }

    @Test
    void testServiceSelfEquality() throws ApplicationException {
        final EqualServiceProxy service = proxierLoader().create(this.introspector()).factory(EqualServiceProxy.class).proxy().get();
        Assertions.assertEquals(service, service);
        Assertions.assertTrue(service.test(service));
    }

    @Test
    void testInterfaceProxySelfEquality() throws ApplicationException {
        final ProxyFactory<EqualInterfaceProxy> factory = proxierLoader().create(this.introspector()).factory(EqualInterfaceProxy.class);
        final Option<EqualInterfaceProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());

        final EqualInterfaceProxy proxyInstance = proxy.get();
        Assertions.assertEquals(proxyInstance, proxyInstance);
        Assertions.assertTrue(proxyInstance.test(proxyInstance));
    }

    @Test
    void testLambdaCanBeProxied() throws NoSuchMethodException, ApplicationException {
        Class<Supplier<String>> supplierClass = (Class<Supplier<String>>) (Class<?>) Supplier.class;
        final StateAwareProxyFactory<Supplier<String>> factory = proxierLoader().create(this.introspector()).factory(supplierClass);
        factory.intercept(Supplier.class.getMethod("get"), context -> "foo");
        final Option<Supplier<String>> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());
        Assertions.assertEquals("foo", proxy.get().get());
    }

    @Test
    void testIsProxyIsTrueIfTypeIsProxy() throws ApplicationException {
        Introspector introspector = this.introspector();
        ApplicationProxier proxier = this.proxierLoader().create(introspector);
        final ProxyFactory<?> factory = proxier.factory(Object.class);
        final Object proxy = factory.proxy().get();

        final boolean instanceIsProxy = proxier.isProxy(proxy);
        Assertions.assertTrue(instanceIsProxy);

        final boolean typeIsProxy = proxier.isProxy(proxy.getClass());
        Assertions.assertTrue(typeIsProxy);
    }

    @Test
    void testIsProxyIsFalseIfTypeIsNormal() {
        Introspector introspector = this.introspector();
        ApplicationProxier proxier = this.proxierLoader().create(introspector);
        final TypeView<?> view = introspector.introspect(Object.class);
        final boolean isProxy = proxier.isProxy(view);
        Assertions.assertFalse(isProxy);
    }
}
