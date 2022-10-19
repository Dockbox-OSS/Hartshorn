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
import org.dockbox.hartshorn.proxy.ProxyCallbackContext;
import org.dockbox.hartshorn.proxy.ProxyFactory;
import org.dockbox.hartshorn.proxy.ProxyInvocationException;
import org.dockbox.hartshorn.proxy.ProxyManager;
import org.dockbox.hartshorn.proxy.StateAwareProxyFactory;
import org.dockbox.hartshorn.proxy.UseProxying;
import org.dockbox.hartshorn.proxy.cglib.CglibProxyFactory;
import org.dockbox.hartshorn.proxy.javassist.JavassistProxyFactory;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

import jakarta.inject.Inject;

@SuppressWarnings("unchecked")
@UseServiceProvision
@UseProxying
@HartshornTest
public class ProxyTests {

    @Inject
    private ApplicationContext applicationContext;

    public static Stream<Arguments> factories() {
        return Stream.of(
                Arguments.of((BiFunction<Class<?>, ApplicationContext, ProxyFactory<?, ?>>) CglibProxyFactory::new),
                Arguments.of((BiFunction<Class<?>, ApplicationContext, ProxyFactory<?, ?>>) JavassistProxyFactory::new)
        );
    }

    @ParameterizedTest
    @MethodSource("factories")
    void testConcreteMethodsCanBeProxied(final BiFunction<Class<?>, ApplicationContext, ProxyFactory<?, ?>> factory) throws ApplicationException, NoSuchMethodException {
        final Method name = ConcreteProxyTarget.class.getMethod("name");
        final ProxyFactory<ConcreteProxyTarget, ?> handler = (ProxyFactory<ConcreteProxyTarget, ?>) factory.apply(ConcreteProxyTarget.class, this.applicationContext);
        handler.intercept(name, context -> "Hartshorn");
        final ConcreteProxyTarget proxy = handler.proxy().get();

        Assertions.assertNotNull(proxy);
        Assertions.assertNotNull(proxy.name());
        Assertions.assertEquals("Hartshorn", proxy.name());
    }

    @ParameterizedTest
    @MethodSource("factories")
    void testFinalMethodsCanNotBeProxied(final BiFunction<Class<?>, ApplicationContext, ProxyFactory<?, ?>> factory) throws ApplicationException, NoSuchMethodException {
        final Method name = FinalProxyTarget.class.getMethod("name");
        final ProxyFactory<FinalProxyTarget, ?> handler = (ProxyFactory<FinalProxyTarget, ?>) factory.apply(FinalProxyTarget.class, this.applicationContext);
        handler.intercept(name, context -> "Hartshorn");
        final FinalProxyTarget proxy = handler.proxy().get();

        Assertions.assertNotNull(proxy);
        Assertions.assertNotNull(proxy.name());
        Assertions.assertNotEquals("Hartshorn", proxy.name());
        Assertions.assertEquals("NotHartshorn", proxy.name());
    }

    public static Stream<Arguments> proxyTypes() {
        final List<Arguments> factories = factories().toList();
        final List<Class<? extends InterfaceProxy>> proxies = List.of(
                InterfaceProxy.class,
                AbstractProxy.class,
                ConcreteProxy.class
        );
        final List<Arguments> matrix = new ArrayList<>();
        for (final Arguments factory : factories) {
            for (final Class<?> proxy : proxies) {
                final Object[] factoryObjects = factory.get();
                final Object[] args = new Object[factoryObjects.length + 1];
                args[0] = proxy;
                System.arraycopy(factoryObjects, 0, args, 1, factoryObjects.length);
                matrix.add(Arguments.of(args));
            }
        }
        return matrix.stream();
    }

    @ParameterizedTest
    @MethodSource("factories")
    void testRecordProxyCannotBeCreated(final BiFunction<Class<?>, ApplicationContext, ProxyFactory<?, ?>> factory) {
        // Records are final and cannot be proxied
        final ProxyFactory<RecordProxy, ?> handler = (ProxyFactory<RecordProxy, ?>) factory.apply(RecordProxy.class, this.applicationContext);
        Assertions.assertThrows(ApplicationException.class, handler::proxy);
    }

    @ParameterizedTest
    @MethodSource("proxyTypes")
    void testEmptyProxyCanCreate(final Class<? extends InterfaceProxy> proxyParent, final BiFunction<Class<?>, ApplicationContext, ProxyFactory<?, ?>> factory) throws ApplicationException {
        final ProxyFactory<InterfaceProxy, ?> handler = (ProxyFactory<InterfaceProxy, ?>) factory.apply(proxyParent, this.applicationContext);
        final InterfaceProxy proxy = handler.proxy().get();
        Assertions.assertNotNull(proxy);
    }

    @ParameterizedTest
    @MethodSource("proxyTypes")
    void testMethodsCanBeDelegatedToOriginalInstance(final Class<? extends InterfaceProxy> proxyType, final BiFunction<Class<?>, ApplicationContext, ProxyFactory<?, ?>> factoryFn) throws ApplicationException {
        final ProxyFactory<InterfaceProxy, ?> factory = (ProxyFactory<InterfaceProxy, ?>) factoryFn.apply(proxyType, this.applicationContext);
        factory.delegate(new ConcreteProxy());
        final Result<InterfaceProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());

        final InterfaceProxy proxyInstance = proxy.get();
        Assertions.assertEquals("concrete", proxyInstance.name());
    }

    @Test
    void testConcreteProxyWithNonDefaultConstructorUsesConstructor() {
        final StateAwareProxyFactory<ConcreteProxyWithNonDefaultConstructor, ?> factory = this.applicationContext.environment().factory(ConcreteProxyWithNonDefaultConstructor.class);

        final TypeView<ConcreteProxyWithNonDefaultConstructor> typeView = this.applicationContext.environment().introspect(ConcreteProxyWithNonDefaultConstructor.class);
        final ConstructorView<ConcreteProxyWithNonDefaultConstructor> constructor = typeView.constructors().all().get(0);
        final Result<ConcreteProxyWithNonDefaultConstructor> proxy = Assertions.assertDoesNotThrow(() -> factory.proxy(constructor, new Object[]{this.applicationContext}));
        Assertions.assertTrue(proxy.present());

        final ConcreteProxyWithNonDefaultConstructor proxyInstance = proxy.get();
        Assertions.assertSame(this.applicationContext, proxyInstance.applicationContext());
    }

    @ParameterizedTest
    @MethodSource("proxyTypes")
    void testMethodsCanBeIntercepted(final Class<? extends InterfaceProxy> proxyType, final BiFunction<Class<?>, ApplicationContext, ProxyFactory<?, ?>> factoryFn) throws ApplicationException, NoSuchMethodException {
        final ProxyFactory<InterfaceProxy, ?> factory = (ProxyFactory<InterfaceProxy, ?>) factoryFn.apply(proxyType, this.applicationContext);
        factory.intercept(proxyType.getMethod("name"), context -> "Hartshorn");
        final Result<InterfaceProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());

        final InterfaceProxy proxyInstance = proxy.get();
        Assertions.assertEquals("Hartshorn", proxyInstance.name());
    }

    @ParameterizedTest
    @MethodSource("proxyTypes")
    void testMethodsCanBeDelegated(final Class<? extends InterfaceProxy> proxyType, final BiFunction<Class<?>, ApplicationContext, ProxyFactory<?, ?>> factoryFn) throws ApplicationException, NoSuchMethodException {
        final ProxyFactory<InterfaceProxy, ?> factory = (ProxyFactory<InterfaceProxy, ?>) factoryFn.apply(proxyType, this.applicationContext);
        factory.delegate(proxyType.getMethod("name"), new ConcreteProxy());
        final Result<InterfaceProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());

        final InterfaceProxy proxyInstance = proxy.get();
        Assertions.assertEquals("concrete", proxyInstance.name());
    }

    @ParameterizedTest
    @MethodSource("factories")
    void testTypesCanBeDelegated(final BiFunction<Class<?>, ApplicationContext, ProxyFactory<?, ?>> factoryFn) throws ApplicationException {
        // Use a custom interface for this type of delegation, as the other proxy types override methods from their parent
        final ProxyFactory<NamedAgedProxy, ?> factory = (ProxyFactory<NamedAgedProxy, ?>) factoryFn.apply(NamedAgedProxy.class, this.applicationContext);
        factory.delegate(AgedProxy.class, () -> 12);
        factory.delegate(NamedProxy.class, () -> "NamedProxy");
        final Result<NamedAgedProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());

        final NamedAgedProxy proxyInstance = proxy.get();
        Assertions.assertEquals(12, proxyInstance.age());
        Assertions.assertEquals("NamedProxy", proxyInstance.name());
    }

    @ParameterizedTest
    @MethodSource("factories")
    void testProxyWillYieldExceptionOnMissingProperty(final BiFunction<Class<?>, ApplicationContext, ProxyFactory<?, ?>> factoryFn) throws ApplicationException {
        // Use a custom interface for this type of delegation, as the other proxy types override methods from their parent
        final ProxyFactory<AgedProxy, ?> factory = (ProxyFactory<AgedProxy, ?>) factoryFn.apply(AgedProxy.class, this.applicationContext);
        final Result<AgedProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());

        final AgedProxy proxyInstance = proxy.get();
        final ProxyInvocationException exception = Assertions.assertThrows(ProxyInvocationException.class, proxyInstance::age);
        Assertions.assertTrue(StringUtilities.notEmpty(exception.getMessage()));
    }

    @ParameterizedTest
    @MethodSource("proxyTypes")
    void testWrapperInterceptionIsCorrect(final Class<? extends InterfaceProxy> proxyType, final BiFunction<Class<?>, ApplicationContext, ProxyFactory<?, ?>> factoryFn) throws NoSuchMethodException, ApplicationException {
        final ProxyFactory<InterfaceProxy, ?> factory = (ProxyFactory<InterfaceProxy, ?>) factoryFn.apply(proxyType, this.applicationContext);
        final AtomicInteger count = new AtomicInteger();
        factory.intercept(proxyType.getMethod("name"), context -> "done");
        factory.intercept(proxyType.getMethod("name"), new MethodWrapper<>() {
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
        final Result<InterfaceProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());

        final InterfaceProxy proxyInstance = proxy.get();
        Assertions.assertEquals("done", proxyInstance.name());
        Assertions.assertEquals(2, count.get());
    }

    @ParameterizedTest
    @MethodSource("proxyTypes")
    void testErrorWrapperInterceptionIsCorrect(final Class<? extends InterfaceProxy> proxyType, final BiFunction<Class<?>, ApplicationContext, ProxyFactory<?, ?>> factoryFn) throws NoSuchMethodException, ApplicationException {
        final ProxyFactory<InterfaceProxy, ?> factory = (ProxyFactory<InterfaceProxy, ?>) factoryFn.apply(proxyType, this.applicationContext);
        final AtomicInteger count = new AtomicInteger();
        factory.intercept(proxyType.getMethod("name"), context -> {
            throw new IllegalStateException("not done");
        });
        factory.intercept(proxyType.getMethod("name"), new MethodWrapper<>() {
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
        final Result<InterfaceProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());

        final InterfaceProxy proxyInstance = proxy.get();
        final IllegalStateException error = Assertions.assertThrows(IllegalStateException.class, proxyInstance::name);
        Assertions.assertEquals("not done", error.getMessage());
        Assertions.assertEquals(2, count.get());
    }

    @ParameterizedTest
    @MethodSource("factories")
    void testProxyManagerTracksInterceptorsAndDelegates(final BiFunction<Class<?>, ApplicationContext, ProxyFactory<?, ?>> factoryFn) throws NoSuchMethodException, ApplicationException {
        final ProxyFactory<NamedAgedProxy, ?> factory = (ProxyFactory<NamedAgedProxy, ?>) factoryFn.apply(NamedAgedProxy.class, this.applicationContext);

        final AgedProxy aged = () -> 12;
        factory.delegate(AgedProxy.class, aged);

        final MethodInterceptor<NamedAgedProxy, ?> named = context -> "NamedProxy";
        factory.intercept(NamedProxy.class.getMethod("name"), named);
        final Result<NamedAgedProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());

        final Proxy<?> proxyInstance = (Proxy<?>) proxy.get();
        final ProxyManager<?> manager = proxyInstance.manager();

        final Result<?> agedDelegate = manager.delegate(AgedProxy.class);
        Assertions.assertTrue(agedDelegate.present());
        Assertions.assertSame(agedDelegate.get(), aged);

        final Result<?> namedInterceptor = manager.interceptor(NamedProxy.class.getMethod("name"));
        Assertions.assertTrue(namedInterceptor.present());
        Assertions.assertSame(namedInterceptor.get(), named);
    }

    @ParameterizedTest
    @MethodSource("proxyTypes")
    void testProxyCanHaveExtraInterfaces(final Class<? extends InterfaceProxy> proxyType, final BiFunction<Class<?>, ApplicationContext, ProxyFactory<?, ?>> factoryFn) throws ApplicationException {
        final ProxyFactory<InterfaceProxy, ?> factory = (ProxyFactory<InterfaceProxy, ?>) factoryFn.apply(proxyType, this.applicationContext);
        factory.implement(DescribedProxy.class);
        final Result<InterfaceProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());

        final InterfaceProxy proxyInstance = proxy.get();
        Assertions.assertTrue(proxyInstance instanceof DescribedProxy);
    }


    @ParameterizedTest
    @MethodSource("proxyTypes")
    void testProxiesAlwaysImplementProxyType(final Class<? extends InterfaceProxy> proxyType, final BiFunction<Class<?>, ApplicationContext, ProxyFactory<?, ?>> factoryFn) throws ApplicationException {
        final ProxyFactory<InterfaceProxy, ?> factory = (ProxyFactory<InterfaceProxy, ?>) factoryFn.apply(proxyType, this.applicationContext);
        final Result<InterfaceProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());
        final InterfaceProxy proxyInstance = proxy.get();
        Assertions.assertTrue(proxyInstance instanceof Proxy);
    }

    @ParameterizedTest
    @MethodSource("proxyTypes")
    void testProxiesExposeManager(final Class<? extends InterfaceProxy> proxyType, final BiFunction<Class<?>, ApplicationContext, ProxyFactory<?, ?>> factoryFn) throws ApplicationException {
        final ProxyFactory<InterfaceProxy, ?> factory = (ProxyFactory<InterfaceProxy, ?>) factoryFn.apply(proxyType, this.applicationContext);
        final Result<InterfaceProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());

        final Proxy<?> proxyInstance = (Proxy<?>) proxy.get();
        Assertions.assertNotNull(proxyInstance.manager());
    }

    @ParameterizedTest
    @MethodSource("proxyTypes")
    void testProxyManagerExposesTargetAndProxyType(final Class<? extends InterfaceProxy> proxyType, final BiFunction<Class<?>, ApplicationContext, ProxyFactory<?, ?>> factoryFn) throws ApplicationException {
        final ProxyFactory<InterfaceProxy, ?> factory = (ProxyFactory<InterfaceProxy, ?>) factoryFn.apply(proxyType, this.applicationContext);
        final Result<InterfaceProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());

        final ProxyManager<InterfaceProxy> manager = ((Proxy<InterfaceProxy>) proxy.get()).manager();
        Assertions.assertNotNull(manager.proxyClass());
        Assertions.assertNotNull(manager.targetClass());

        Assertions.assertNotEquals(proxyType, manager.proxyClass());
        Assertions.assertEquals(proxyType, manager.targetClass());

        Assertions.assertTrue(this.applicationContext.environment().isProxy(manager.proxyClass()));
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

    @ParameterizedTest
    @MethodSource("factories")
    void testConcreteProxySelfEquality(final BiFunction<Class<?>, ApplicationContext, ProxyFactory<?, ?>> factoryFn) throws ApplicationException {
        final ProxyFactory<EqualProxy, ?> factory = (ProxyFactory<EqualProxy, ?>) factoryFn.apply(EqualProxy.class, this.applicationContext);
        final Result<EqualProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());

        final EqualProxy proxyInstance = proxy.get();
        Assertions.assertEquals(proxyInstance, proxyInstance);
        Assertions.assertTrue(proxyInstance.test(proxyInstance));
    }

    @ParameterizedTest
    @MethodSource("factories")
    void testServiceSelfEquality(final BiFunction<Class<?>, ApplicationContext, ProxyFactory<?, ?>> factory) throws ApplicationException {
        final EqualServiceProxy service = (EqualServiceProxy) factory.apply(EqualServiceProxy.class, this.applicationContext).proxy().get();
        Assertions.assertEquals(service, service);
        Assertions.assertTrue(service.test(service));
    }

    @ParameterizedTest
    @MethodSource("factories")
    void testInterfaceProxySelfEquality(final BiFunction<Class<?>, ApplicationContext, ProxyFactory<?, ?>> factoryFn) throws ApplicationException {
        final ProxyFactory<EqualInterfaceProxy, ?> factory = (ProxyFactory<EqualInterfaceProxy, ?>) factoryFn.apply(EqualInterfaceProxy.class, this.applicationContext);
        final Result<EqualInterfaceProxy> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());

        final EqualInterfaceProxy proxyInstance = proxy.get();
        Assertions.assertEquals(proxyInstance, proxyInstance);
        Assertions.assertTrue(proxyInstance.test(proxyInstance));
    }

    @ParameterizedTest
    @MethodSource("factories")
    void testLambdaCanBeProxied(final BiFunction<Class<?>, ApplicationContext, ProxyFactory<?, ?>> factoryFn) throws NoSuchMethodException, ApplicationException {
        final StateAwareProxyFactory<Supplier<String>, ?> factory = (StateAwareProxyFactory<Supplier<String>, ?>) factoryFn.apply(Supplier.class, this.applicationContext);
        factory.intercept(Supplier.class.getMethod("get"), context -> "foo");
        final Result<Supplier<String>> proxy = factory.proxy();
        Assertions.assertTrue(proxy.present());
        Assertions.assertEquals("foo", proxy.get().get());
    }
}
