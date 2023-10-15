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

package org.dockbox.hartshorn.testsuite;

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

import org.dockbox.hartshorn.application.ApplicationBuilder;
import org.dockbox.hartshorn.application.StandardApplicationBuilder;
import org.dockbox.hartshorn.application.StandardApplicationContextConstructor;
import org.dockbox.hartshorn.application.StandardApplicationContextConstructor.Configurer;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.context.SimpleApplicationContext;
import org.dockbox.hartshorn.application.environment.ContextualApplicationEnvironment;
import org.dockbox.hartshorn.component.ContextualComponentPopulator;
import org.dockbox.hartshorn.component.processing.ComponentFinalizingPostProcessor;
import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.component.processing.ComponentPreProcessor;
import org.dockbox.hartshorn.component.processing.ComponentProcessor;
import org.dockbox.hartshorn.component.processing.ServiceActivator;
import org.dockbox.hartshorn.introspect.ExecutableElementContextParameterLoader;
import org.dockbox.hartshorn.util.ApplicationBoundParameterLoaderContext;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.ApplicationRuntimeException;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.introspect.util.ParameterLoader;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.option.Attempt;
import org.dockbox.hartshorn.util.option.Option;
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

import jakarta.inject.Inject;

public class HartshornLifecycleExtension implements
        ParameterResolver,
        BeforeAllCallback, AfterAllCallback,
        BeforeEachCallback, AfterEachCallback {

    private ApplicationContext applicationContext;

    @Override
    public void beforeEach(ExtensionContext context) throws ApplicationException {
        Class<?> testClass = context.getTestClass().orElse(null);
        Object testInstance = context.getTestInstance().orElse(null);
        Method testMethod = context.getTestMethod().orElse(null);
        this.beforeLifecycle(testClass, testInstance, testMethod);
    }

    @Override
    public void afterEach(ExtensionContext context) throws ApplicationException {
        this.afterLifecycle();
    }

    @Override
    public void beforeAll(ExtensionContext context) throws ApplicationException {
        if (this.isClassLifecycle(context)) {
            Class<?> testClass = context.getTestClass().orElse(null);
            Object testInstance = context.getTestInstance().orElse(null);
            this.beforeLifecycle(testClass, testInstance);
        }
    }

    @Override
    public void afterAll(ExtensionContext context) throws ApplicationException {
        if (this.isClassLifecycle(context)) {
            this.afterLifecycle();
        }
    }

    private boolean isClassLifecycle(ExtensionContext context) {
        Optional<Lifecycle> lifecycle = context.getTestInstanceLifecycle();
        return lifecycle.isPresent() && Lifecycle.PER_CLASS == lifecycle.get();
    }

    protected void beforeLifecycle(Class<?> testClass, Object testInstance, AnnotatedElement... testComponentSources)
            throws ApplicationException {
        if (testClass == null) {
            throw new IllegalArgumentException("Test class cannot be null");
        }

        List<AnnotatedElement> elements = new ArrayList<>(Arrays.asList(testComponentSources));
        elements.add(testClass);

        this.invokeModifiers(testClass);

        ApplicationBuilder<?> applicationBuilder = this.prepareFactory(testClass, elements);
        ApplicationContext applicationContext = applicationBuilder.create();

        if (applicationContext == null) {
            throw new IllegalStateException("Could not create application context");
        }

        applicationContext.bind(HartshornLifecycleExtension.class).singleton(this);

        if (testInstance != null) {
            this.populateTestInstance(testInstance, applicationContext);
        }

        this.applicationContext = applicationContext;
    }

    protected void afterLifecycle() throws ApplicationException {
        Mockito.clearAllCaches();
        if (this.applicationContext != null) {
            this.applicationContext.close();
            this.applicationContext = null;
        }
        TestCustomizer.resetAll();
    }

    private void invokeModifiers(Class<?> testClass) throws ApplicationException {
        List<Method> methods = Arrays.stream(testClass.getMethods())
                .filter(method -> method.isAnnotationPresent(ModifyApplication.class))
                .toList();

        for (Method factoryModifier : methods) {
            doCheckFactoryModifierValid(factoryModifier);

            if (!factoryModifier.canAccess(null)) {
                factoryModifier.setAccessible(true);
            }

            Class<?>[] parameters = factoryModifier.getParameterTypes();
            if (parameters.length == 0) {
                Attempt.of(() -> (Customizer<StandardApplicationBuilder.Configurer>) factoryModifier.invoke(null), Throwable.class)
                        .mapError(ApplicationException::new)
                        .rethrow();
            }
            else {
                throw new InvalidFactoryModifierException("Invalid parameter count for " + factoryModifier.getName() + ", expected 0, got " + parameters.length + ".");
            }

            factoryModifier.setAccessible(false);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        Optional<Method> testMethod = extensionContext.getTestMethod();
        if (testMethod.isEmpty()) {
            return false;
        }

        MethodView<?, ?> method = this.applicationContext.environment().introspector().introspect(testMethod.get());
        return method.annotations().has(Inject.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        if (this.applicationContext == null) {
            throw new IllegalStateException("No active state present");
        }

        Optional<Method> testMethod = extensionContext.getTestMethod();
        if (testMethod.isEmpty()) {
            throw new ParameterResolutionException("Test method was not provided to runner");
        }

        ParameterLoader<ApplicationBoundParameterLoaderContext> parameterLoader = new ExecutableElementContextParameterLoader();
        MethodView<?, ?> executable = this.applicationContext.environment().introspector().introspect(testMethod.get());
        ApplicationBoundParameterLoaderContext parameterLoaderContext = new ApplicationBoundParameterLoaderContext(executable,
                extensionContext.getTestInstance().orElse(null), this.applicationContext);

        return parameterLoader.loadArgument(parameterLoaderContext, parameterContext.getIndex());
    }

    protected void populateTestInstance(Object instance, ApplicationContext applicationContext) {
        ContextualComponentPopulator componentPopulator = ComponentFinalizingPostProcessor.createComponentPopulator(applicationContext);
        componentPopulator.populate(instance);
    }

    private ApplicationBuilder<?> prepareFactory(Class<?> testClass, List<AnnotatedElement> testComponentSources) {
        Customizer<StandardApplicationBuilder.Configurer> builderCustomizer = Customizer.useDefaults();
        builderCustomizer = builderCustomizer.compose(builder -> {
            customizeBuilderWithTestSources(testClass, testComponentSources, builder);

            Customizer<StandardApplicationContextConstructor.Configurer> customizer = new ContextConstructorCustomizer(testClass, testComponentSources);
            builder.constructor(StandardApplicationContextConstructor.create(customizer.compose(TestCustomizer.CONSTRUCTOR.customizer())));
        });

        return StandardApplicationBuilder.create(builderCustomizer.compose(TestCustomizer.BUILDER.customizer()));
    }

    private static void doCheckFactoryModifierValid(Method factoryModifier) {
        if (!Modifier.isStatic(factoryModifier.getModifiers())) {
            throw new IllegalStateException("Expected " + factoryModifier.getName() + " to be static.");
        }

        if (!factoryModifier.getReturnType().equals(Void.TYPE)) {
            throw new InvalidFactoryModifierException("Invalid return type for " + factoryModifier.getName() + ", expected void");
        }
    }

    private static void customizeBuilderWithTestSources(Class<?> testClass, List<AnnotatedElement> testComponentSources,
                                                        StandardApplicationBuilder.Configurer builder) {

        // Note: initial default, may be overwritten by test component sources below
        builder.mainClass(testClass);

        for (AnnotatedElement element : testComponentSources) {
            if (element == null) {
                continue;
            }

            Option.of(element.getAnnotation(HartshornTest.class))
                    .map(HartshornTest::mainClass)
                    .filter(mainClass -> mainClass != Void.class)
                    .peek(builder::mainClass);

            Option.of(element.getAnnotation(TestProperties.class))
                    .map(TestProperties::value)
                    .peek(properties -> builder.arguments(args -> args.addAll(properties)));
        }
    }

    private record ContextConstructorCustomizer(
            Class<?> testClass, List<AnnotatedElement> testComponentSources) implements Customizer<Configurer> {

        @Override
        public void configure(Configurer constructor) {
            Customizer<ContextualApplicationEnvironment.Configurer> environmentCustomizer = environment -> {
                environment.enableBanner(); // Disable banner for tests, to avoid unnecessary noise
                environment.enableBatchMode(); // Enable batch mode, to make use of additional caching
                environment.showStacktraces(); // Enable stacktraces for tests, to make debugging easier
                environment.applicationFSProvider(new TemporaryFileSystemProvider());

                Customizer<SimpleApplicationContext.Configurer> applicationContextCustomizer = applicationContext -> {
                    configureDefaultBindings(applicationContext, testComponentSources);
                };
                environment.applicationContext(SimpleApplicationContext.create(applicationContextCustomizer.compose(TestCustomizer.APPLICATION_CONTEXT.customizer())));
            };
            constructor.environment(ContextualApplicationEnvironment.create(environmentCustomizer.compose(TestCustomizer.ENVIRONMENT.customizer())));

            for (AnnotatedElement element : this.testComponentSources) {
                this.customizeWithComponentSource(constructor, element);
            }

            this.customizeActivators(constructor);
        }

        private void configureDefaultBindings(SimpleApplicationContext.Configurer applicationContext, List<AnnotatedElement> testComponentSources) {
            for(AnnotatedElement testComponentSource : testComponentSources) {
                if (testComponentSource.isAnnotationPresent(TestComponents.class)) {
                    TestBinding[] bindings = testComponentSource.getAnnotation(TestComponents.class).bindings();
                    applicationContext.defaultBindings((context, binder) -> {
                        for(TestBinding binding : bindings) {
                            //noinspection unchecked
                            binder.bind(binding.type()).to((Class) binding.implementation());
                        }
                    });
                }
            }
        }

        private void customizeActivators(Configurer constructor) {
            Class<?> next = this.testClass;
            Set<Annotation> serviceActivators = new HashSet<>();
            while (next != null) {
                Arrays.stream(next.getAnnotations())
                        .filter(annotation -> annotation.annotationType().isAnnotationPresent(ServiceActivator.class))
                        .forEach(serviceActivators::add);

                next = next.getSuperclass();
            }
            constructor.activators(activators -> {
                activators.addAll(serviceActivators);
            });
        }

        private void customizeWithComponentSource(Configurer constructor, AnnotatedElement element) {
            Option<HartshornTest> testDecorator = Option.of(element.getAnnotation(HartshornTest.class));
            if (testDecorator.present()) {
                this.registerProcessors(constructor, testDecorator);
                constructor.scanPackages(config -> config.addAll(testDecorator.get().scanPackages()));
                constructor.includeBasePackages(testDecorator.get().includeBasePackages());
            }
            registerStandaloneComponents(constructor, element);
        }

        private void registerProcessors(Configurer constructor, Option<HartshornTest> testDecorator) {
            List<Class<? extends ComponentProcessor>> processors = List.of(testDecorator.get().processors());
            List<ComponentPreProcessor> preProcessors = this.filterProcessors(ComponentPreProcessor.class, processors);
            List<ComponentPostProcessor> postProcessors = this.filterProcessors(ComponentPostProcessor.class, processors);

            constructor.componentPreProcessors(config -> config.addAll(preProcessors));
            constructor.componentPostProcessors(config -> config.addAll(postProcessors));
        }

        private static void registerStandaloneComponents(Configurer constructor, AnnotatedElement element) {
            if (element.isAnnotationPresent(TestComponents.class)) {
                TestComponents testComponents = element.getAnnotation(TestComponents.class);
                constructor.standaloneComponents(components -> components.addAll(testComponents.components()));
            }
        }

        private <T extends ComponentProcessor> List<T> filterProcessors(Class<T> type, List<Class<? extends ComponentProcessor>> processors) {
            return processors.stream()
                    .filter(type::isAssignableFrom)
                    .map(processor -> Attempt.of(() -> processor.getConstructor().newInstance(), Throwable.class)
                            .mapError(ApplicationRuntimeException::new)
                            .rethrow()
                            .cast(type)
                            .get())
                    .toList();
        }
    }
}
