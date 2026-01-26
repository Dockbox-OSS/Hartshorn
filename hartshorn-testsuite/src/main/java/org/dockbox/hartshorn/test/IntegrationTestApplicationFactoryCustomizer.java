/*
 * Copyright 2019-2026 the original author or authors.
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

package org.dockbox.hartshorn.test;

import org.dockbox.hartshorn.inject.ImmutableInjectorConfiguration;
import org.dockbox.hartshorn.inject.ObjectFactory;
import org.dockbox.hartshorn.inject.ReflectionObjectFactory;
import org.dockbox.hartshorn.launchpad.SimpleApplicationContext;
import org.dockbox.hartshorn.launchpad.activation.ModuleActivator;
import org.dockbox.hartshorn.launchpad.environment.ConfigurableApplicationEnvironment;
import org.dockbox.hartshorn.launchpad.launch.StandardApplicationContextFactory;
import org.dockbox.hartshorn.launchpad.properties.EnvironmentProfilesPropertyRegistryFactory;
import org.dockbox.hartshorn.profiles.support.CompositeProfileNameResolver;
import org.dockbox.hartshorn.profiles.support.FromPropertyProfileNameResolver;
import org.dockbox.hartshorn.test.annotations.TestComponents;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.dockbox.hartshorn.util.configure.Customizer;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Customizer for the {@link StandardApplicationContextFactory} that configures the application
 * context for integration tests.
 *
 * @param testClass the test class
 * @param testComponentSources the component sources to use for the test
 * @param applicationCustomizer the customizer for the test application
 */
public record IntegrationTestApplicationFactoryCustomizer(
    Class<?> testClass,
    List<AnnotatedElement> testComponentSources,
    TestApplicationCustomizer applicationCustomizer
) implements Customizer<StandardApplicationContextFactory.Configurer> {

    private static final ObjectFactory OBJECT_FACTORY = new ReflectionObjectFactory();

    @Override
    public void configure(StandardApplicationContextFactory.Configurer constructor) {
        Customizer<ConfigurableApplicationEnvironment.Configurer> environmentCustomizer =
            environment -> {
                environment.injectorConfiguration(ImmutableInjectorConfiguration.create(injector -> {
                    injector.disableBanner();
                    injector.enableBatchMode();
                    injector.showStacktraces();
                    this.applicationCustomizer.customizeInjector(injector);
                }));

                environment.applicationFSProvider(new TemporaryFileSystemProvider());
                environment.applicationContext(SimpleApplicationContext.create(
                    this.applicationCustomizer::customizeApplication
                ));

                environment.propertyRegistryFactory(
                    EnvironmentProfilesPropertyRegistryFactory.create(propertyRegistryFactory -> {
                        propertyRegistryFactory.profileNameResolver(
                            new CompositeProfileNameResolver(
                                new FromPropertyProfileNameResolver(),
                                new FromTestAnnotationProfileNameResolver(this.testComponentSources)
                            ));
                    })
                );
            };
        constructor.environment(ConfigurableApplicationEnvironment.create(
            environmentCustomizer.compose(this.applicationCustomizer::customizeEnvironment)
        ));

        for (AnnotatedElement element : this.testComponentSources) {
            this.customizeWithComponentSource(constructor, element);
        }

        this.customizeModuleActivators(constructor);
    }

    private void customizeModuleActivators(
        StandardApplicationContextFactory.Configurer constructor
    ) {
        Class<?> next = this.testClass;
        Set<Annotation> moduleActivators = new HashSet<>();
        while (next != null) {
            Arrays.stream(next.getAnnotations())
                .filter(annotation -> annotation.annotationType()
                    .isAnnotationPresent(ModuleActivator.class))
                .forEach(moduleActivators::add);

            next = next.getSuperclass();
        }
        constructor.moduleActivators(activators -> {
            activators.addAll(moduleActivators);
        });
    }

    private void customizeWithComponentSource(
        StandardApplicationContextFactory.Configurer constructor,
        AnnotatedElement element
    ) {
        HartshornIntegrationTest testDecorator = element
            .getAnnotation(HartshornIntegrationTest.class);
        if (testDecorator != null) {
            this.registerProcessors(constructor, testDecorator);
            constructor.includePackages(config -> {
                config.addAll(testDecorator.includePackages());
            });
            constructor.excludePackages(config -> {
                config.addAll(testDecorator.excludePackages());
            });
            constructor.includeBasePackages(testDecorator.includeBasePackages());
        }

        registerStandaloneComponents(constructor, element);
    }

    private void registerProcessors(
        StandardApplicationContextFactory.Configurer constructor,
        HartshornIntegrationTest testDecorator
    ) {
        constructor.componentPreProcessors(config ->
            config.addAll(this.instantiateAll(List.of(testDecorator.componentPreProcessors())))
        );
        constructor.componentPostProcessors(config ->
            config.addAll(this.instantiateAll(List.of(testDecorator.componentPostProcessors())))
        );
        constructor.binderPostProcessors(config ->
            config.addAll(this.instantiateAll(List.of(testDecorator.binderPostProcessors())))
        );
    }

    private static void registerStandaloneComponents(
        StandardApplicationContextFactory.Configurer constructor,
        AnnotatedElement element
    ) {
        if (element.isAnnotationPresent(TestComponents.class)) {
            TestComponents testComponents = element.getAnnotation(TestComponents.class);
            constructor.standaloneComponents(
                components -> components.addAll(testComponents.value())
            );
        }
    }

    private <T> List<T> instantiateAll(List<Class<? extends T>> types) {
        List<T> result = new ArrayList<>();
        for (Class<? extends T> type : types) {
            result.add(OBJECT_FACTORY.create(type));
        }
        return result;
    }
}
