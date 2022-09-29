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

import org.dockbox.hartshorn.application.ApplicationBuilder;
import org.dockbox.hartshorn.application.InitializingContext;
import org.dockbox.hartshorn.application.ServiceImpl;
import org.dockbox.hartshorn.application.StandardApplicationBuilder;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentLocator;
import org.dockbox.hartshorn.component.ComponentLocatorImpl;
import org.dockbox.hartshorn.component.ComponentPopulator;
import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.component.processing.ComponentPreProcessor;
import org.dockbox.hartshorn.component.processing.ComponentProcessor;
import org.dockbox.hartshorn.component.processing.ServiceActivator;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
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

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import jakarta.inject.Inject;

public class HartshornLifecycleExtension implements
        ParameterResolver,
        BeforeAllCallback, AfterAllCallback,
        BeforeEachCallback, AfterEachCallback {

    private ApplicationContext applicationContext;

    @Override
    public void beforeEach(final ExtensionContext context) {
        final Class<?> testClass = context.getTestClass().orElse(null);
        final Object testInstance = context.getTestInstance().orElse(null);
        final Method testMethod = context.getTestMethod().orElse(null);
        this.beforeLifecycle(testClass, testInstance, testMethod);
    }

    @Override
    public void afterEach(final ExtensionContext context) throws IOException {
        this.afterLifecycle();
    }

    @Override
    public void beforeAll(final ExtensionContext context) {
        if (this.isClassLifecycle(context)) {
            final Class<?> testClass = context.getTestClass().orElse(null);
            final Object testInstance = context.getTestInstance().orElse(null);
            this.beforeLifecycle(testClass, testInstance);
        }
    }

    @Override
    public void afterAll(final ExtensionContext context) throws IOException {
        if (this.isClassLifecycle(context)) {
            this.afterLifecycle();
        }
    }

    private boolean isClassLifecycle(final ExtensionContext context) {
        final Optional<Lifecycle> lifecycle = context.getTestInstanceLifecycle();
        return lifecycle.isPresent() && Lifecycle.PER_CLASS.equals(lifecycle.get());
    }

    protected void beforeLifecycle(final Class<?> testClass, final Object testInstance, final AnnotatedElement... testComponentSources) {
        if (testClass == null) {
            throw new IllegalArgumentException("Test class cannot be null");
        }

        final ApplicationBuilder<?, ?> applicationBuilder = this.prepareFactory(testClass, testComponentSources);
        final ApplicationContext applicationContext = HartshornLifecycleExtension.createTestContext(applicationBuilder, testClass).orNull();
        if (applicationContext == null) {
            if (applicationContext == null) throw new IllegalStateException("Could not create application context");
        }

        applicationContext.bind(HartshornLifecycleExtension.class).singleton(this);

        if (testInstance != null) {
            this.populateTestInstance(testInstance, applicationContext);
        }

        this.applicationContext = applicationContext;
    }

    protected void afterLifecycle() throws IOException {
        Mockito.clearAllCaches();
        if (this.applicationContext != null) {
            this.applicationContext.close();
            this.applicationContext = null;
        }
    }

    @Override
    public boolean supportsParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext) throws ParameterResolutionException {
        final Optional<Method> testMethod = extensionContext.getTestMethod();
        if (testMethod.isEmpty()) return false;

        final MethodView<?, ?> method = this.applicationContext.environment().introspect(testMethod.get());
        return method.annotations().has(Inject.class);
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

    public static Result<ApplicationContext> createTestContext(final ApplicationBuilder<?, ?> applicationBuilder, Class<?> activator) {
        Class<?> next = activator;
        final Set<Annotation> serviceActivators = new HashSet<>();
        while (next != null) {
            Arrays.stream(next.getAnnotations())
                    .filter(annotation -> annotation.annotationType().isAnnotationPresent(ServiceActivator.class))
                    .forEach(serviceActivators::add);

            next = next.getSuperclass();
        }
        applicationBuilder.serviceActivators(serviceActivators);

        final ApplicationContext context = applicationBuilder.mainClass(activator).create();
        return Result.of(context);
    }

    private ApplicationBuilder<?, ?> prepareFactory(final Class<?> testClass, final AnnotatedElement... testComponentSources) {
        ApplicationBuilder<?, ?> applicationBuilder = new StandardApplicationBuilder()
                .loadDefaults()
                .applicationFSProvider(ctx -> new JUnitFSProvider())
                .componentLocator(ctx -> this.getComponentLocator(ctx, testComponentSources))
                .serviceActivator(new ServiceImpl());

        final List<AnnotatedElement> elements = new ArrayList<>(Arrays.asList(testComponentSources));
        elements.add(testClass);

        final List<String> arguments = new ArrayList<>();
        final ApplicationBuilder<?, ?> finalApplicationBuilder = applicationBuilder;

        for (final AnnotatedElement element : elements) {
            Result.of(element.getAnnotation(HartshornTest.class))
                    .present(annotation -> {
                        for (final Class<? extends ComponentProcessor> processor : annotation.processors()) {
                            final ComponentProcessor componentProcessor = Result.of(() -> processor.getConstructor().newInstance()).rethrowUnchecked().get();
                            if (componentProcessor instanceof ComponentPreProcessor preProcessor) {
                                finalApplicationBuilder.preProcessor(preProcessor);
                            }
                            else if (componentProcessor instanceof ComponentPostProcessor postProcessor) {
                                finalApplicationBuilder.postProcessor(postProcessor);
                            }
                        }

                        finalApplicationBuilder.prefixes(annotation.scanPackages());
                    });

            Result.of(element.getAnnotation(TestProperties.class))
                    .present(annotation -> arguments.addAll(Arrays.asList(annotation.value())));
        }

        applicationBuilder.arguments(arguments.toArray(new String[0]));

        final List<Method> methods = Arrays.stream(testClass.getMethods())
                .filter(method -> method.isAnnotationPresent(HartshornFactory.class))
                .toList();

        for (final Method factoryModifier : methods) {
            if (!Modifier.isStatic(factoryModifier.getModifiers())) {
                throw new IllegalStateException("Expected " + factoryModifier.getName() + " to be static.");
            }
            if (ApplicationBuilder.class.isAssignableFrom(factoryModifier.getReturnType())) {
                if (!factoryModifier.canAccess(null)) factoryModifier.setAccessible(true);

                final Class<?>[] parameters = factoryModifier.getParameterTypes();
                if (parameters.length == 0) {
                    applicationBuilder = Result.of(() -> (ApplicationBuilder<?, ?>) factoryModifier.invoke(null)).rethrowUnchecked().orNull();
                }
                else if (ApplicationBuilder.class.isAssignableFrom(parameters[0])) {
                    final ApplicationBuilder<?, ?> factoryArg = applicationBuilder;
                    applicationBuilder = Result.of(() -> (ApplicationBuilder<?, ?>) factoryModifier.invoke(null, factoryArg)).rethrowUnchecked().orNull();
                }
                else {
                    throw new InvalidFactoryModifierException("parameters", parameters[0]);
                }
                factoryModifier.setAccessible(false);
            }
            else {
                throw new InvalidFactoryModifierException("return type", factoryModifier.getReturnType());
            }
        }

        return applicationBuilder;
    }

    private ComponentLocator getComponentLocator(final InitializingContext context, final AnnotatedElement... testComponentSources) {
        final ComponentLocator componentLocator = new ComponentLocatorImpl(context);

        for (final AnnotatedElement testComponentSource : testComponentSources) {
            if (testComponentSource == null) continue;
            if (testComponentSource.isAnnotationPresent(TestComponents.class)) {
                final TestComponents testComponents = testComponentSource.getAnnotation(TestComponents.class);
                for (final Class<?> component : testComponents.value()) {
                    componentLocator.register(component);
                }
            }
        }

        return componentLocator;
    }
}
