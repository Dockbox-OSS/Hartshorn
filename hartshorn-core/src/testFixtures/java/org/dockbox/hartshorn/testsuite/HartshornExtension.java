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

package org.dockbox.hartshorn.testsuite;

import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.annotations.activate.Activator;
import org.dockbox.hartshorn.core.annotations.service.ServiceActivator;
import org.dockbox.hartshorn.core.boot.HartshornApplicationFactory;
import org.dockbox.hartshorn.core.context.ApplicationContext;
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

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
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
 * @since 4.2.5
 */
@Activator
public class HartshornExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    private Method activeMethod;
    private ApplicationContext applicationContext;

    @Override
    public void beforeEach(final ExtensionContext context) throws Exception {
        final Optional<Class<?>> testClass = context.getTestClass();
        if (testClass.isEmpty()) throw new IllegalStateException("Test class was not provided to runner");

        final ApplicationContext applicationContext = createContext(testClass.get()).orNull();
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

    public static Exceptional<ApplicationContext> createContext(final Class<?> activator) throws IOException {
        TypeContext<?> applicationActivator = TypeContext.of(activator);

        final HartshornApplicationFactory factory = new HartshornApplicationFactory()
                .loadDefaults()
                .applicationFSProvider(new JUnitFSProvider());

        if (applicationActivator.annotation(Activator.class).absent()) {
            applicationActivator = TypeContext.of(HartshornExtension.class);
            final Set<Annotation> serviceActivators = TypeContext.of(activator).annotations().stream()
                    .filter(annotation -> TypeContext.of(annotation.annotationType()).annotation(ServiceActivator.class).present())
                    .collect(Collectors.toSet());

            factory.serviceActivators(serviceActivators);
        }

        final ApplicationContext context = factory.activator(applicationActivator).create();
        return Exceptional.of(context);
    }
}
