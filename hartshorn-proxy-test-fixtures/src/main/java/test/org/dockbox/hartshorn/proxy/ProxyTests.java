/*
 * Copyright 2019-2025 the original author or authors.
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

import org.assertj.core.api.InstanceOfAssertFactories;
import org.dockbox.hartshorn.proxy.Proxy;
import org.dockbox.hartshorn.proxy.ProxyFactory;
import org.dockbox.hartshorn.proxy.ProxyManager;
import org.dockbox.hartshorn.proxy.ProxyOrchestrator;
import org.dockbox.hartshorn.proxy.ProxyOrchestratorLoader;
import org.dockbox.hartshorn.proxy.advice.intercept.MethodInterceptor;
import org.dockbox.hartshorn.proxy.advice.wrap.MethodWrapper;
import org.dockbox.hartshorn.proxy.advice.wrap.ProxyCallbackContext;
import org.dockbox.hartshorn.proxy.constraint.ProxyConstraintViolationException;
import org.dockbox.hartshorn.proxy.lookup.StateAwareProxyFactory;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.function.CheckedSupplier;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import test.org.dockbox.hartshorn.proxy.support.basic.ConcreteProxyWithNonDefaultConstructor;
import test.org.dockbox.hartshorn.proxy.support.basic.DescribedProxy;
import test.org.dockbox.hartshorn.proxy.support.equals.AbstractEqualProxy;
import test.org.dockbox.hartshorn.proxy.support.equals.EqualInterfaceProxy;
import test.org.dockbox.hartshorn.proxy.support.equals.EqualProxy;
import test.org.dockbox.hartshorn.proxy.support.inheritance.multi.AgedProxy;
import test.org.dockbox.hartshorn.proxy.support.inheritance.multi.NamedAgedProxy;
import test.org.dockbox.hartshorn.proxy.support.inheritance.multi.NamedProxy;
import test.org.dockbox.hartshorn.proxy.support.inheritance.single.AbstractProxy;
import test.org.dockbox.hartshorn.proxy.support.inheritance.single.ConcreteProxy;
import test.org.dockbox.hartshorn.proxy.support.inheritance.single.InterfaceProxy;
import test.org.dockbox.hartshorn.proxy.support.standard.ConcreteProxyTarget;
import test.org.dockbox.hartshorn.proxy.support.standard.FinalClassProxyTarget;
import test.org.dockbox.hartshorn.proxy.support.standard.FinalMethodProxyTarget;
import test.org.dockbox.hartshorn.proxy.support.standard.RecordProxy;
import test.org.dockbox.hartshorn.proxy.support.standard.SealedProxy;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

/**
 * Tests for the default behavior of proxies of various types.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
@SuppressWarnings("unchecked")
public abstract class ProxyTests {

    protected abstract ProxyOrchestratorLoader orchestratorLoader();

    protected abstract Introspector introspector();

    @Test
    void concreteMethodsCanBeProxied() throws Exception {
        Method name = ConcreteProxyTarget.class.getMethod("name");
        ProxyFactory<ConcreteProxyTarget> handler = this.orchestratorLoader()
            .create(this.introspector())
            .factory(ConcreteProxyTarget.class);
        handler.advisors().method(name).intercept(_ -> "Hartshorn");
        ConcreteProxyTarget proxy = handler.proxy().get();

        assertThat(proxy).isNotNull();
        assertThat(proxy.name()).isNotNull();
        assertThat(proxy.name()).isEqualTo("Hartshorn");
    }

    @Test
    void finalMethodsCanNotBeProxied() throws Exception {
        Method name = FinalMethodProxyTarget.class.getMethod("name");
        ProxyFactory<FinalMethodProxyTarget> handler = this.orchestratorLoader()
            .create(this.introspector())
            .factory(FinalMethodProxyTarget.class);

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> handler
                .advisors()
                .method(name)
                .intercept(_ -> "Hartshorn"));
    }

    public static Stream<Arguments> proxyTypes() {
        return Stream.of(
            Arguments.of(InterfaceProxy.class),
            Arguments.of(AbstractProxy.class),
            Arguments.of(ConcreteProxy.class)
        );
    }

    @Test
    void recordProxyCannotBeCreated() {
        // Records are and cannot be proxied
        ProxyFactory<RecordProxy> handler = this.orchestratorLoader()
                .create(this.introspector())
                .factory(RecordProxy.class);
        assertThatExceptionOfType(ProxyConstraintViolationException.class)
                .isThrownBy(handler::proxy);
    }

    @Test
    void sealedClassProxyCannotBeCreated() {
        // Sealed classes only allow for a limited number of subclasses and should not be proxied
        ProxyFactory<SealedProxy> handler = this.orchestratorLoader()
                .create(this.introspector())
                .factory(SealedProxy.class);
        assertThatExceptionOfType(ProxyConstraintViolationException.class)
                .isThrownBy(handler::proxy);
    }

    @Test
    void finalClassProxyCannotBeCreated() {
        // Final classes cannot be extended and should not be proxied
        ProxyFactory<FinalClassProxyTarget> handler = this.orchestratorLoader()
            .create(this.introspector())
            .factory(FinalClassProxyTarget.class);
        assertThatExceptionOfType(ProxyConstraintViolationException.class)
                .isThrownBy(handler::proxy);
    }

    @ParameterizedTest
    @MethodSource("proxyTypes")
    void emptyProxyCanCreate(Class<? extends InterfaceProxy> proxyParent)
        throws Exception {
        ProxyFactory<? extends InterfaceProxy> handler =
            this.orchestratorLoader().create(this.introspector()).factory(proxyParent);
        InterfaceProxy proxy = handler.proxy().get();
        assertThat(proxy).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("proxyTypes")
    void methodsCanBeDelegatedToOriginalInstance(Class<InterfaceProxy> proxyType)
        throws Exception {
        ProxyFactory<InterfaceProxy> factory =
            this.orchestratorLoader().create(this.introspector()).factory(proxyType);
        factory.advisors().type().delegate(new ConcreteProxy());
        Option<InterfaceProxy> proxy = factory.proxy();
        assertThat(proxy.present()).isTrue();

        InterfaceProxy proxyInstance = proxy.get();
        assertThat(proxyInstance.name()).isEqualTo("concrete");
    }

    @Test
    void concreteProxyWithNonDefaultConstructorUsesConstructor() throws Exception {
        StateAwareProxyFactory<ConcreteProxyWithNonDefaultConstructor> factory =
            this.orchestratorLoader()
                .create(this.introspector())
                .factory(ConcreteProxyWithNonDefaultConstructor.class);

        TypeView<ConcreteProxyWithNonDefaultConstructor> typeView = this.introspector()
                .introspect(ConcreteProxyWithNonDefaultConstructor.class);
        ConstructorView<ConcreteProxyWithNonDefaultConstructor> constructor = typeView
                .constructors()
                .all()
                .getFirst();
        Option<ConcreteProxyWithNonDefaultConstructor> proxy = factory.proxy(
                constructor,
                new Object[]{"Hello world"}
        );
        assertThat(proxy.present()).isTrue();

        ConcreteProxyWithNonDefaultConstructor proxyInstance = proxy.get();
        assertThat(proxyInstance.message()).isEqualTo("Hello world");
    }

    @ParameterizedTest
    @MethodSource("proxyTypes")
    void methodsCanBeIntercepted(Class<? extends InterfaceProxy> proxyType)
        throws Exception {
        ProxyFactory<? extends InterfaceProxy> factory = this.orchestratorLoader()
                .create(this.introspector())
                .factory(proxyType);

        factory.advisors()
                .method(proxyType.getMethod("name"))
                .intercept(_ -> "Hartshorn");
        Option<? extends InterfaceProxy> proxy = factory.proxy();
        assertThat(proxy.present()).isTrue();

        InterfaceProxy proxyInstance = proxy.get();
        assertThat(proxyInstance.name()).isEqualTo("Hartshorn");
    }

    @ParameterizedTest
    @MethodSource("proxyTypes")
    void methodsCanBeDelegated(Class<? extends InterfaceProxy> proxyType)
        throws Exception {
        ProxyFactory<InterfaceProxy> factory =
            (ProxyFactory<InterfaceProxy>) this.orchestratorLoader()
                .create(this.introspector())
                .factory(proxyType);
        factory.advisors().method(proxyType.getMethod("name")).delegate(new ConcreteProxy());
        Option<? extends InterfaceProxy> proxy = factory.proxy();
        assertThat(proxy.present()).isTrue();

        InterfaceProxy proxyInstance = proxy.get();
        assertThat(proxyInstance.name()).isEqualTo("concrete");
    }

    @Test
    void typesCanBeDelegated() throws Exception {
        // Use a custom interface for this type of delegation, as the other proxy types override
        // methods from their parent
        ProxyFactory<NamedAgedProxy> factory =
            this.orchestratorLoader().create(this.introspector()).factory(NamedAgedProxy.class);
        factory.advisors().type(AgedProxy.class).delegate(() -> 12);
        factory.advisors().type(NamedProxy.class).delegate(() -> "NamedProxy");
        Option<NamedAgedProxy> proxy = factory.proxy();
        assertThat(proxy.present()).isTrue();

        NamedAgedProxy proxyInstance = proxy.get();
        assertThat(proxyInstance.age()).isEqualTo(12);
        assertThat(proxyInstance.name()).isEqualTo("NamedProxy");
    }

    @ParameterizedTest
    @MethodSource("proxyTypes")
    void wrapperInterceptionIsCorrect(Class<? extends InterfaceProxy> proxyType)
        throws Exception {
        ProxyFactory<InterfaceProxy> factory =
            (ProxyFactory<InterfaceProxy>) this.orchestratorLoader()
                .create(this.introspector())
                .factory(proxyType);
        AtomicInteger count = new AtomicInteger();
        factory.advisors().method(proxyType.getMethod("name")).intercept(_ -> "done");
        factory.advisors().method(proxyType.getMethod("name")).wrapAround(new MethodWrapper<>() {
            @Override
            public void acceptBefore(ProxyCallbackContext<InterfaceProxy> context) {
                assertThat(count.getAndIncrement()).isZero();
            }

            @Override
            public void acceptAfter(ProxyCallbackContext<InterfaceProxy> context) {
                assertThat(count.getAndIncrement()).isOne();
            }

            @Override
            public void acceptError(ProxyCallbackContext<InterfaceProxy> context) {
                // Not thrown
                fail();
            }
        });
        Option<InterfaceProxy> proxy = factory.proxy();
        assertThat(proxy.present()).isTrue();

        InterfaceProxy proxyInstance = proxy.get();
        assertThat(proxyInstance.name()).isEqualTo("done");
        assertThat(count.get()).isEqualTo(2);
    }

    @ParameterizedTest
    @MethodSource("proxyTypes")
    void errorWrapperInterceptionIsCorrect(Class<? extends InterfaceProxy> proxyType)
        throws Exception {
        ProxyFactory<InterfaceProxy> factory =
            (ProxyFactory<InterfaceProxy>) this.orchestratorLoader()
                .create(this.introspector())
                .factory(proxyType);
        AtomicInteger count = new AtomicInteger();
        factory.advisors().method(proxyType.getMethod("name")).intercept(_ -> {
            throw new IllegalStateException("not done");
        });
        factory.advisors().method(proxyType.getMethod("name")).wrapAround(new MethodWrapper<>() {
            @Override
            public void acceptBefore(ProxyCallbackContext<InterfaceProxy> context) {
                assertThat(count.getAndIncrement()).isZero();
            }

            @Override
            public void acceptAfter(ProxyCallbackContext<InterfaceProxy> context) {
                fail();
            }

            @Override
            public void acceptError(ProxyCallbackContext<InterfaceProxy> context) {
                Throwable error = context.error();
                assertThat(error)
                        .asInstanceOf(InstanceOfAssertFactories.type(IllegalStateException.class))
                        .extracting(Throwable::getMessage);
                assertThat(count.getAndIncrement()).isOne();
            }
        });
        Option<InterfaceProxy> proxy = factory.proxy();
        assertThat(proxy.present()).isTrue();

        InterfaceProxy proxyInstance = proxy.get();
        IllegalStateException error = assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(proxyInstance::name)
                .actual();
        assertThat(error.getMessage()).isEqualTo("not done");
        assertThat(count.get()).isEqualTo(2);
    }

    @Test
    void proxyManagerTracksInterceptorsAndDelegates()
        throws Exception {
        ProxyFactory<NamedAgedProxy> factory =
            this.orchestratorLoader().create(this.introspector()).factory(NamedAgedProxy.class);

        AgedProxy aged = () -> 12;
        factory.advisors().type(AgedProxy.class).delegate(aged);

        MethodInterceptor<NamedAgedProxy, Object> named = _ -> "NamedProxy";
        factory.advisors().method(NamedProxy.class.getMethod("name")).intercept(named);
        Option<NamedAgedProxy> proxy = factory.proxy();
        assertThat(proxy.present()).isTrue();

        Proxy<?> proxyInstance = (Proxy<?>) proxy.get();
        ProxyManager<?> manager = proxyInstance.manager();

        Option<?> agedDelegate = manager.advisor()
            .resolver()
            .type(AgedProxy.class)
            .delegate();
        assertThat(agedDelegate.present()).isTrue();
        assertThat(aged).isSameAs(agedDelegate.get());

        Option<?> namedInterceptor = manager.advisor()
            .resolver()
            .method(NamedProxy.class.getMethod("name"))
            .interceptor();
        assertThat(namedInterceptor.present()).isTrue();
        assertThat(named).isSameAs(namedInterceptor.get());
    }

    @ParameterizedTest
    @MethodSource("proxyTypes")
    void proxyCanHaveExtraInterfaces(Class<? extends InterfaceProxy> proxyType)
        throws Exception {
        ProxyFactory<InterfaceProxy> factory =
            (ProxyFactory<InterfaceProxy>) this.orchestratorLoader()
                .create(this.introspector())
                .factory(proxyType);
        factory.implement(DescribedProxy.class);
        Option<InterfaceProxy> proxy = factory.proxy();
        assertThat(proxy.present()).isTrue();

        InterfaceProxy proxyInstance = proxy.get();
        assertThat(proxyInstance).isInstanceOf(DescribedProxy.class);
    }

    @ParameterizedTest
    @MethodSource("proxyTypes")
    void proxiesAlwaysImplementProxyType(Class<? extends InterfaceProxy> proxyType)
        throws Exception {
        ProxyFactory<InterfaceProxy> factory =
            (ProxyFactory<InterfaceProxy>) this.orchestratorLoader()
                .create(this.introspector())
                .factory(proxyType);
        Option<InterfaceProxy> proxy = factory.proxy();
        assertThat(proxy.present()).isTrue();
        InterfaceProxy proxyInstance = proxy.get();
        assertThat(proxyInstance).isInstanceOf(Proxy.class);
    }

    @ParameterizedTest
    @MethodSource("proxyTypes")
    void proxiesExposeManager(Class<? extends InterfaceProxy> proxyType)
        throws Exception {
        ProxyFactory<InterfaceProxy> factory =
            (ProxyFactory<InterfaceProxy>) this.orchestratorLoader()
                .create(this.introspector())
                .factory(proxyType);
        Option<InterfaceProxy> proxy = factory.proxy();
        assertThat(proxy.present()).isTrue();

        Proxy<?> proxyInstance = (Proxy<?>) proxy.get();
        assertThat(proxyInstance.manager()).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("proxyTypes")
    void proxyManagerExposesTargetAndProxyType(Class<? extends InterfaceProxy> proxyType)
        throws Exception {
        ProxyFactory<InterfaceProxy> factory =
            (ProxyFactory<InterfaceProxy>) this.orchestratorLoader()
                .create(this.introspector())
                .factory(proxyType);
        Option<InterfaceProxy> proxy = factory.proxy();
        assertThat(proxy.present()).isTrue();

        ProxyManager<InterfaceProxy> manager = ((Proxy<InterfaceProxy>) proxy.get()).manager();
        assertThat(manager.proxyClass()).isNotNull();
        assertThat(manager.targetClass()).isNotNull();

        assertThat(manager.proxyClass()).isNotEqualTo(proxyType);
        assertThat(manager.targetClass()).isSameAs(proxyType);

        assertThat(manager.orchestrator().isProxy(manager.proxyClass())).isTrue();
    }

    @Test
    void interfaceProxyDoesNotEqual() throws Exception {
        DemoServiceA serviceA1 = this.createProxy(DemoServiceA.class);
        DemoServiceA serviceA2 = this.createProxy(DemoServiceA.class);

        assertThat(serviceA2)
                .isNotSameAs(serviceA1)
                .isNotEqualTo(serviceA1);
    }

    @Test
    void abstractClassProxyDoesNotEqual() throws Exception {
        DemoServiceB serviceC1 = this.createProxy(DemoServiceB.class);
        DemoServiceB serviceC2 = this.createProxy(DemoServiceB.class);

        assertThat(serviceC2)
                .isNotSameAs(serviceC1)
                .isNotEqualTo(serviceC1);
    }

    @Test
    void concreteClassProxyWithoutDelegateDoesNotEqual() throws Exception {
        DemoServiceC serviceB1 = this.createProxy(DemoServiceC.class);
        DemoServiceC serviceB2 = this.createProxy(DemoServiceC.class);

        assertThat(serviceB2)
                .isNotSameAs(serviceB1)
                .isNotEqualTo(serviceB1);
    }

    @Test
    public void concreteClassProxyWithNonEqualsImplementedDelegateDoesNotEqual()
        throws Exception {
        CheckedSupplier<DemoServiceC> supplier =
            () -> this.orchestratorLoader().create(this.introspector())
                .factory(DemoServiceC.class)
                .advisors(advisors -> advisors.type().delegate(new DemoServiceC()))
                .proxy()
                .get();

        DemoServiceC serviceC3 = supplier.get();
        DemoServiceC serviceC4 = supplier.get();

        assertThat(serviceC4)
                .isNotSameAs(serviceC3)
                .isNotEqualTo(serviceC3);
    }

    @Test
    void concreteClassProxyWithDelegateDoesNotEqual() throws Exception {
        CheckedSupplier<DemoServiceD> supplier =
            () -> this.orchestratorLoader().create(this.introspector())
                .factory(DemoServiceD.class)
                .advisors(advisors -> advisors.type().delegate(new DemoServiceD("name")))
                .proxy()
                .get();

        DemoServiceD serviceD1 = supplier.get();
        DemoServiceD serviceD2 = supplier.get();

        assertThat(serviceD2)
                .isNotSameAs(serviceD1)
                .isEqualTo(serviceD1);
    }

    private <T> T createProxy(Class<T> type) throws ApplicationException {
        return this.orchestratorLoader().create(this.introspector()).factory(type).proxy().get();
    }

    public interface DemoServiceA {
    }

    public abstract static class DemoServiceB {
    }

    public static class DemoServiceC {
    }

    public static class DemoServiceD {
        private String name;

        public DemoServiceD(String name) {
            this.name = name;
        }

        @SuppressWarnings("unused")
        public DemoServiceD() {
            // Default constructor for proxying. Note that this is typically handled by providing a
            // constructor to the proxy factory, but this is a test, so we're not doing that.
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || this.getClass() != other.getClass()) {
                return false;
            }
            DemoServiceD service = (DemoServiceD) other;
            return this.name.equals(service.name);
        }

        @Override
        public int hashCode() {
            return this.name.hashCode();
        }
    }

    @Test
    void concreteProxySelfEquality() throws Exception {
        ProxyFactory<EqualProxy> factory =
            this.orchestratorLoader().create(this.introspector()).factory(EqualProxy.class);
        Option<EqualProxy> proxy = factory.proxy();
        assertThat(proxy.present()).isTrue();

        EqualProxy proxyInstance = proxy.get();
        assertThat(proxyInstance).isEqualTo(proxyInstance);
        assertThat(proxyInstance.test(proxyInstance)).isTrue();
    }

    @Test
    void serviceSelfEquality() throws Exception {
        AbstractEqualProxy service = this.orchestratorLoader()
            .create(this.introspector())
            .factory(AbstractEqualProxy.class)
            .proxy()
            .get();
        assertThat(service).isEqualTo(service);
        assertThat(service.test(service)).isTrue();
    }

    @Test
    void interfaceProxySelfEquality() throws Exception {
        ProxyFactory<EqualInterfaceProxy> factory = this.orchestratorLoader()
            .create(this.introspector())
            .factory(EqualInterfaceProxy.class);
        Option<EqualInterfaceProxy> proxy = factory.proxy();
        assertThat(proxy.present()).isTrue();

        EqualInterfaceProxy proxyInstance = proxy.get();
        assertThat(proxyInstance).isEqualTo(proxyInstance);
        assertThat(proxyInstance.test(proxyInstance)).isTrue();
    }

    @Test
    void lambdaCanBeProxied() throws Exception {
        Class<Supplier<String>> supplierClass = (Class<Supplier<String>>) (Class<?>) Supplier.class;
        StateAwareProxyFactory<Supplier<String>> factory =
            this.orchestratorLoader().create(this.introspector()).factory(supplierClass);
        factory.advisors().method(Supplier.class.getMethod("get")).intercept(_ -> "foo");
        Option<Supplier<String>> proxy = factory.proxy();
        assertThat(proxy.present()).isTrue();
        assertThat(proxy.get().get()).isEqualTo("foo");
    }

    @Test
    void isProxyIsTrueIfTypeIsProxy() throws Exception {
        Introspector introspector = this.introspector();
        ProxyOrchestrator orchestrator = this.orchestratorLoader().create(introspector);
        ProxyFactory<?> factory = orchestrator.factory(Object.class);
        Object proxy = factory.proxy().get();

        boolean instanceIsProxy = orchestrator.isProxy(proxy);
        assertThat(instanceIsProxy).isTrue();

        boolean typeIsProxy = orchestrator.isProxy(proxy.getClass());
        assertThat(typeIsProxy).isTrue();
    }

    @Test
    void isProxyIsFalseIfTypeIsNormal() {
        Introspector introspector = this.introspector();
        ProxyOrchestrator orchestrator = this.orchestratorLoader().create(introspector);
        TypeView<?> view = introspector.introspect(Object.class);
        boolean isProxy = orchestrator.isProxy(view);
        assertThat(isProxy).isFalse();
    }
}
