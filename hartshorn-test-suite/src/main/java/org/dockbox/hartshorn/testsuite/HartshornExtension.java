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

import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.annotations.activate.Activator;
import org.dockbox.hartshorn.core.annotations.activate.ServiceActivator;
import org.dockbox.hartshorn.core.boot.ApplicationFactory;
import org.dockbox.hartshorn.core.boot.HartshornApplicationFactory;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.AccessModifier;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.mockito.Mockito;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * This extension is used to create a unique {@link ApplicationContext} for each test. This ensures that the
 * {@link ApplicationContext} is reset after each test, to avoid leaking state between tests.
 *
 * <p>If a test is annotated with {@link InjectTest} or {@link Inject}, its parameters will be resolved using the
 * active {@link ApplicationContext}. Likewise, fields annotated with {@link Inject} will be resolved using the
 * active {@link ApplicationContext}. Like the {@link ApplicationContext}, fields are reset after each test.
 *
 * <p>If a test class is annotated with {@link Activator}, it is used as the primary application activator. Which
 * allows the test class to define its own activators. If the test class is not annotated with {@link Activator},
 * the {@link HartshornExtension} is used as the primary activator, which doesn't activate any non-default
 * services.
 *
 * <p>After each test, the active {@link Mockito} cache is cleared. This ensures that the mocks are reset between
 * tests.
 *
 * <p>This extension is automatically applied by the {@link HartshornTest} annotation.
 *
 * @author Guus Lieben
 * @since 22.1
 */
@Activator
public class HartshornExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    private Method activeMethod;
    private ApplicationContext applicationContext;

    @Override
    public void beforeEach(final ExtensionContext context) throws Exception {
        final ApplicationFactory<?, ?> applicationFactory = this.prepareFactory(context);

        final Optional<Class<?>> testClass = context.getTestClass();
        if (testClass.isEmpty()) throw new IllegalStateException("Test class was not provided to runner");

        final ApplicationContext applicationContext = createContext(applicationFactory, testClass.get()).orNull();
        if (applicationContext == null) throw new IllegalStateException("Could not create application context");

        applicationContext.bind(Key.of(HartshornExtension.class), this);

        final Optional<Object> testInstance = context.getTestInstance();
        testInstance.ifPresent(applicationContext::populate);

        final Optional<Method> testMethod = context.getTestMethod();
        if (testMethod.isEmpty()) throw new IllegalStateException("Test method was not provided to runner");

        this.activeMethod = testMethod.get();
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterEach(final ExtensionContext context) {
        // To ensure static mocking does not affect other tests
        Mockito.clearAllCaches();

        this.activeMethod = null;
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
        if (!testMethod.get().equals(this.activeMethod)) throw new IllegalStateException("Active context is not the same as the test method");

        return this.applicationContext.get(parameterContext.getParameter().getType());
    }

    public static Exceptional<ApplicationContext> createContext(final ApplicationFactory<?, ?> applicationFactory, final Class<?> activator) {
        TypeContext<?> applicationActivator = TypeContext.of(activator);

        if (applicationActivator.annotation(Activator.class).absent()) {
            applicationActivator = TypeContext.of(HartshornExtension.class);
            final Set<Annotation> serviceActivators = TypeContext.of(activator).annotations().stream()
                    .filter(annotation -> TypeContext.of(annotation.annotationType()).annotation(ServiceActivator.class).present())
                    .collect(Collectors.toSet());

            applicationFactory.serviceActivators(serviceActivators);
        }

        final ApplicationContext context = applicationFactory.activator(applicationActivator).create();
        return Exceptional.of(context);
    }

    public ApplicationFactory<?, ?> prepareFactory(final ExtensionContext context) throws Exception {
        ApplicationFactory<?, ?> applicationFactory = new HartshornApplicationFactory()
                .loadDefaults()
                .applicationFSProvider(new JUnitFSProvider());

        if (context.getTestClass().isPresent()) {
            final List<? extends MethodContext<?, ?>> factoryModifiers = TypeContext.of(context.getTestClass().get()).methods(HartshornFactory.class);
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
        }

        return applicationFactory;
    }
}
