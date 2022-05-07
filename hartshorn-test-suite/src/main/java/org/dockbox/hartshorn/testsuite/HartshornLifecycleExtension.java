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

package org.dockbox.hartshorn.testsuite;

import org.dockbox.hartshorn.application.Activator;
import org.dockbox.hartshorn.application.ApplicationFactory;
import org.dockbox.hartshorn.application.ServiceImpl;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentLocator;
import org.dockbox.hartshorn.component.ComponentLocatorImpl;
import org.dockbox.hartshorn.component.ComponentPopulator;
import org.dockbox.hartshorn.component.processing.ServiceActivator;
import org.dockbox.hartshorn.inject.binding.ComponentBinding;
import org.dockbox.hartshorn.util.Exceptional;
import org.dockbox.hartshorn.util.reflect.AccessModifier;
import org.dockbox.hartshorn.util.reflect.MethodContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.mockito.Mockito;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.inject.Inject;

@Activator
public class HartshornLifecycleExtension implements
        ParameterResolver,
        BeforeAllCallback, AfterAllCallback,
        BeforeEachCallback, AfterEachCallback {

    private ApplicationContext applicationContext;

    @Override
    public void beforeEach(final ExtensionContext context) throws Exception {
        final Class<?> testClass = context.getTestClass().orElse(null);
        final Object testInstance = context.getTestInstance().orElse(null);
        final Method testMethod = context.getTestMethod().orElse(null);
        this.beforeLifecycle(testClass, testInstance, testMethod);
    }

    @Override
    public void afterEach(final ExtensionContext context) {
        this.afterLifecycle();
    }

    @Override
    public void beforeAll(final ExtensionContext context) throws Exception {
        if (this.isClassLifecycle(context)) {
            final Class<?> testClass = context.getTestClass().orElse(null);
            final Object testInstance = context.getTestInstance().orElse(null);
            this.beforeLifecycle(testClass, testInstance);
        }
    }

    @Override
    public void afterAll(final ExtensionContext context) {
        if (this.isClassLifecycle(context)) {
            this.afterLifecycle();
        }
    }

    private boolean isClassLifecycle(final ExtensionContext context) {
        final Optional<Lifecycle> lifecycle = context.getTestInstanceLifecycle();
        return lifecycle.isPresent() && Lifecycle.PER_CLASS.equals(lifecycle.get());
    }

    protected void beforeLifecycle(final Class<?> testClass, final Object testInstance, final AnnotatedElement... testComponentSources) throws Exception {
        if (testClass == null) {
            throw new IllegalArgumentException("Test class cannot be null");
        }

        final ApplicationFactory applicationFactory = this.prepareFactory(testClass, testComponentSources);
        final ApplicationContext applicationContext = HartshornLifecycleExtension.createTestContext(applicationFactory, testClass).orNull();
        if (applicationContext == null) {
            if (applicationContext == null) throw new IllegalStateException("Could not create application context");
        }

        applicationContext.bind(HartshornLifecycleExtension.class).singleton(this);

        if (testInstance != null) {
            this.populateTestInstance(testInstance, applicationContext);
        }

        this.applicationContext = applicationContext;
    }

    protected void afterLifecycle() {
        Mockito.clearAllCaches();
        this.applicationContext = null;
    }

    @Override
    public boolean supportsParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext) throws ParameterResolutionException {
        final Optional<Method> testMethod = extensionContext.getTestMethod();
        if (testMethod.isEmpty()) return false;

        return MethodContext.of(testMethod.get()).annotation(Inject.class).present();
    }

    @Override
    public Object resolveParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext) throws ParameterResolutionException {
        if (this.applicationContext == null) throw new IllegalStateException("No active state present");

        final Optional<Method> testMethod = extensionContext.getTestMethod();
        if (testMethod.isEmpty()) throw new ParameterResolutionException("Test method was not provided to runner");

        return this.applicationContext.get(parameterContext.getParameter().getType());
    }

    protected void populateTestInstance(final Object instance, final ApplicationContext applicationContext) {
        final ComponentPopulator populator = applicationContext.get(ComponentPopulator.class);
        populator.populate(instance);
    }

    public static Exceptional<ApplicationContext> createTestContext(final ApplicationFactory<?, ?> applicationFactory, final Class<?> activator) {
        TypeContext<?> applicationActivator = TypeContext.of(activator);

        if (applicationActivator.annotation(Activator.class).absent()) {
            applicationActivator = TypeContext.of(HartshornLifecycleExtension.class);
            final Set<Annotation> serviceActivators = TypeContext.of(activator).annotations().stream()
                    .filter(annotation -> TypeContext.of(annotation.annotationType()).annotation(ServiceActivator.class).present())
                    .collect(Collectors.toSet());

            applicationFactory.serviceActivators(serviceActivators);
        }

        final ApplicationContext context = applicationFactory.activator(applicationActivator).create();
        return Exceptional.of(context);
    }

    private ApplicationFactory<?, ?> prepareFactory(final Class<?> testClass, final AnnotatedElement... testComponentSources) throws Exception {
        ApplicationFactory<?, ?> applicationFactory = new TestApplicationFactory()
                .loadDefaults()
                .applicationFSProvider(new JUnitFSProvider())
                .componentLocator(applicationContext -> this.getComponentLocator(applicationContext, testComponentSources));

        final List<? extends MethodContext<?, ?>> factoryModifiers = TypeContext.of(testClass).methods(HartshornFactory.class);
        for (final MethodContext<?, ?> factoryModifier : factoryModifiers) {
            if (!factoryModifier.has(AccessModifier.STATIC)) {
                throw new IllegalStateException("Factory modifiers must be static");
            }
            if (factoryModifier.returnType().childOf(ApplicationFactory.class)) {

                final Method jlrMethod = factoryModifier.method();
                if (!jlrMethod.canAccess(null)) jlrMethod.setAccessible(true);

                final LinkedList<TypeContext<?>> parameters = factoryModifier.parameterTypes();
                if (parameters.isEmpty()) {
                    applicationFactory = (ApplicationFactory<?, ?>) factoryModifier.invokeStatic().rethrowUnchecked().orNull();
                }
                else if (parameters.get(0).childOf(ApplicationFactory.class)) {
                    applicationFactory = (ApplicationFactory<?, ?>) factoryModifier.invokeStatic(applicationFactory).rethrowUnchecked().orNull();
                }
                else {
                    throw new IllegalStateException("Invalid parameters for @HartshornFactory modifier, expected " + ApplicationFactory.class.getSimpleName() + " but got " + parameters.get(0).name());
                }

                jlrMethod.setAccessible(false);
            }
            else {
                throw new IllegalStateException("Invalid return type for @HartshornFactory modifier, expected " + ApplicationFactory.class.getSimpleName() + " but got " + factoryModifier.returnType().name());
            }
        }

        return applicationFactory;
    }

    private ComponentLocator getComponentLocator(final ApplicationContext applicationContext, final AnnotatedElement... testComponentSources) {
        final ComponentLocator componentLocator = new ComponentLocatorImpl(applicationContext);

        ((TestApplicationContext) applicationContext).addActivator(new ServiceImpl());

        final Consumer<TestComponents> testComponentsConsumer = testComponents -> {
            for (final Class<?> testComponent : testComponents.value()) {
                componentLocator.register(testComponent);

                if (applicationContext instanceof TestApplicationContext) {
                    final TypeContext<?> testComponentType = TypeContext.of(testComponent);
                    testComponentType.annotation(ComponentBinding.class).present(binding -> {
                        ((TestApplicationContext) applicationContext).handleBinder(testComponentType, binding);
                    });
                }
            }
        };

        for (final AnnotatedElement testComponentSource : testComponentSources) {
            if (testComponentSource == null) continue;
            if (testComponentSource.isAnnotationPresent(TestComponents.class)) {
                testComponentsConsumer.accept(testComponentSource.getAnnotation(TestComponents.class));
            }
        }

        return componentLocator;
    }
}
