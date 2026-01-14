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

package test.org.dockbox.hartshorn.launchpad;

import ch.qos.logback.classic.spi.ILoggingEvent;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.LoggingExceptionHandler;
import org.dockbox.hartshorn.inject.annotations.Component;
import org.dockbox.hartshorn.inject.component.ComponentRegistry;
import org.dockbox.hartshorn.inject.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.inject.processing.ComponentPreProcessor;
import org.dockbox.hartshorn.inject.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.inject.provider.ComponentProvider;
import org.dockbox.hartshorn.inject.provider.PostProcessingComponentProvider;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.DelegatingApplicationContext;
import org.dockbox.hartshorn.launchpad.HartshornApplication;
import org.dockbox.hartshorn.launchpad.HartshornApplicationConfigurer;
import org.dockbox.hartshorn.launchpad.ProcessableApplicationContext;
import org.dockbox.hartshorn.launchpad.activation.ModuleActivator;
import org.dockbox.hartshorn.launchpad.banner.HartshornLogoBanner;
import org.dockbox.hartshorn.launchpad.environment.ConfigurableApplicationEnvironment;
import org.dockbox.hartshorn.properties.PropertyRegistry;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.configure.Customizer;
import org.dockbox.hartshorn.util.introspect.scan.TypeReferenceCollectorContext;
import org.dockbox.hartshorn.util.introspect.scan.classpath.ClasspathTypeReferenceCollector;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.util.types.TypeUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class ApplicationConfigurerTests {

    private static ApplicationContext createApplication(Customizer<HartshornApplicationConfigurer> customizer) {
        return HartshornApplication.createApplication(ApplicationConfigurerTests.class)
            .initialize(customizer.compose(config -> {
                config.includeBasePackages(false);
            }));
    }

    @Test
    @DisplayName("Customizer should be able to modify arguments provided to the application")
    void argumentsCustomizer() {
        ApplicationContext applicationContext = createApplication(configuration -> {
            configuration.arguments(arguments -> {
                arguments.add("sample.x.y=z");
            });
        });
        PropertyRegistry propertyRegistry = applicationContext.environment().propertyRegistry();
        assertThat(propertyRegistry.contains("sample.x.y")).isTrue();
        String value = propertyRegistry.value("sample.x.y")
            .orElseThrow(() -> new AssertionError("Property not found"));
        assertThat(value).isEqualTo("z");
    }

    @Test
    @DisplayName("Customizer should be able to modify module activators")
    void moduleActivatorsCustomizer() {
        ApplicationContext applicationContext = createApplication(configuration -> {
            configuration.activators(activators -> {
                activators.add(TypeUtils.annotation(UseSampleActivator.class));
            });
        });
        assertThat(applicationContext.activators().hasActivator(UseSampleActivator.class)).isTrue();
    }

    @Test
    @DisplayName("Customizer should be able to modify default pre-processors")
    void preProcessorsCustomizer() {
        SamplePreProcessor processor = new SamplePreProcessor();
        ApplicationContext applicationContext = createApplication(configuration -> {
            configuration.componentPreProcessors(preProcessors -> {
                preProcessors.add(processor);
            });
        });
        ProcessableApplicationContext processableApplicationContext = assertThat(applicationContext)
                .asInstanceOf(InstanceOfAssertFactories.type(ProcessableApplicationContext.class))
                .actual();
        MultiMap<Integer, ComponentPreProcessor> processors = processableApplicationContext
                .defaultProvider()
                .processorRegistry()
                .preProcessors();
        assertThat(processors.containsValue(processor)).isTrue();
    }

    @Test
    @DisplayName("Customizer should be able to modify default post-processors")
    void postProcessorsCustomizer() {
        SamplePostProcessor processor = new SamplePostProcessor();
        ApplicationContext applicationContext = createApplication(configuration -> {
            configuration.componentPostProcessors(postProcessors -> {
                postProcessors.add(processor);
            });
        });
        DelegatingApplicationContext processableApplicationContext = assertThat(applicationContext)
                .asInstanceOf(InstanceOfAssertFactories.type(DelegatingApplicationContext.class))
                .actual();
        ComponentProvider componentProvider = processableApplicationContext.componentProvider();
        PostProcessingComponentProvider postProcessingComponentProvider =
                assertThat(componentProvider)
                        .asInstanceOf(InstanceOfAssertFactories.type(
                                PostProcessingComponentProvider.class
                        ))
                        .actual();
        MultiMap<Integer, ComponentPostProcessor> processors =
            postProcessingComponentProvider.processorRegistry().postProcessors();
        assertThat(processors.containsValue(processor)).isTrue();
    }

    @Test
    @DisplayName("Customizer should be able to modify standalone components")
    void standaloneComponentsCustomizer() {
        ApplicationContext applicationContext = createApplication(configuration -> {
            configuration.standaloneComponents(components -> {
                components.add(DummyUnmanagedComponent.class);
            });
        });
        ComponentRegistry componentRegistry = applicationContext.get(ComponentRegistry.class);
        assertThat(componentRegistry.container(DummyUnmanagedComponent.class).present()).isTrue();
    }

    @Test
    @DisplayName("Customizer should be able to modify scanned packages")
    void scannedPackagesCustomizer() {
        final String dummyPackage = "test.dummy.package";
        ApplicationContext applicationContext = createApplication(configuration -> {
            configuration.scanPackages(packages -> {
                packages.add(dummyPackage);
            });
        });
        Option<TypeReferenceCollectorContext> collectorContextCandidate =
            applicationContext.firstContext(TypeReferenceCollectorContext.class);
        assertThat(collectorContextCandidate.present()).isTrue();

        TypeReferenceCollectorContext collectorContext = collectorContextCandidate.get();
        Set<String> packages = collectorContext.collectors().stream()
            .filter(ClasspathTypeReferenceCollector.class::isInstance)
            .map(ClasspathTypeReferenceCollector.class::cast)
            .flatMap(scanner -> scanner.packageNames().stream())
            .collect(Collectors.toSet());
        assertThat(packages).contains(dummyPackage);
    }

    @Test
    @DisplayName("Customizer should be able to enable banner printing")
    void bannerPrintingCustomizer() {
        Logger logger = LoggerFactory.getLogger(ApplicationConfigurerTests.class);
        OutputCaptureAppender appender = OutputCaptureAppender.registerForLogger(logger);
        createApplication(applicationConfigurer -> {
            // Banners are always disabled in CI environments. To avoid false negatives, simulate a non-CI environment
            applicationConfigurer.isBuildEnvironment(false);
            applicationConfigurer.enableBanner();
        });

        String expectedMessage = String.join("\n", new HartshornBannerAccessor().lines());
        for (ILoggingEvent event : appender.events()) {
            if (event.getMessage().equals(expectedMessage)) {
                return;
            }
        }
        fail("Expected message was not found in log output");
    }

    @Test
    @DisplayName("Customizer should be able to disable banner printing")
    void bannerPrintingDisabledCustomizer() {
        Logger logger = LoggerFactory.getLogger(ApplicationConfigurerTests.class);
        OutputCaptureAppender appender = OutputCaptureAppender.registerForLogger(logger);
        createApplication(applicationConfigurer -> {
            // Banners are always disabled in CI environments. To avoid false positives, simulate a non-CI environment
            applicationConfigurer.isBuildEnvironment(false);
            applicationConfigurer.disableBanner();
        });

        String expectedMessage = String.join("\n", new HartshornBannerAccessor().lines());
        for (ILoggingEvent event : appender.events()) {
            if (event.getMessage().equals(expectedMessage)) {
                fail("Unexpected message found in log output");
            }
        }
    }

    @Test
    @DisplayName("Customizer should be able to enable batch mode")
    void batchModeCustomizer() {
        ApplicationContext applicationContext =
            createApplication(HartshornApplicationConfigurer::enableBatchMode);
        assertThat(applicationContext.environment().isBatchMode()).isTrue();
    }

    @Test
    @DisplayName("Customizer should be able to disable batch mode")
    void batchModeDisabledCustomizer() {
        ApplicationContext applicationContext =
            createApplication(HartshornApplicationConfigurer::disableBatchMode);
        assertThat(applicationContext.environment().isBatchMode()).isFalse();
    }

    @Test
    @DisplayName("Customizer should be able to enable strict mode")
    void strictModeCustomizer() {
        ApplicationContext applicationContext =
            createApplication(HartshornApplicationConfigurer::enableStrictMode);
        assertThat(applicationContext.environment().isStrictMode()).isTrue();
    }

    @Test
    @DisplayName("Customizer should be able to disable strict mode")
    void strictModeDisabledCustomizer() {
        ApplicationContext applicationContext =
            createApplication(HartshornApplicationConfigurer::disableStrictMode);
        assertThat(applicationContext.environment().isStrictMode()).isFalse();
    }

    @Test
    @DisplayName("Customizer should be able to enable stacktraces")
    void showStacktracesCustomizer() {
        ApplicationContext applicationContext =
            createApplication(HartshornApplicationConfigurer::showStacktraces);
        ConfigurableApplicationEnvironment configurableApplicationEnvironment =
            assertThat(applicationContext.environment())
                    .asInstanceOf(InstanceOfAssertFactories.type(ConfigurableApplicationEnvironment.class))
                    .actual();
        LoggingExceptionHandler exceptionHandler =
            assertThat(configurableApplicationEnvironment.exceptionHandler())
                    .asInstanceOf(InstanceOfAssertFactories.type(LoggingExceptionHandler.class))
                    .actual();
        assertThat(exceptionHandler.printStackTraces()).isTrue();
    }

    @Test
    @DisplayName("Customizer should be able to disable stacktraces")
    void hideStacktracesCustomizer() {
        ApplicationContext applicationContext = createApplication(
                HartshornApplicationConfigurer::hideStacktraces
        );

        ConfigurableApplicationEnvironment configurableApplicationEnvironment =
            assertThat(applicationContext.environment())
                    .asInstanceOf(InstanceOfAssertFactories.type(
                            ConfigurableApplicationEnvironment.class
                    ))
                    .actual();

        LoggingExceptionHandler exceptionHandler =
            assertThat(configurableApplicationEnvironment.exceptionHandler())
                    .asInstanceOf(InstanceOfAssertFactories.type(LoggingExceptionHandler.class))
                    .actual();
        assertThat(exceptionHandler.printStackTraces()).isFalse();
    }

    @Test
    @DisplayName("Customizer should be able to indicate build environment")
    void buildEnvironmentCustomizer() {
        ApplicationContext applicationContext = createApplication(configuration -> {
            configuration.isBuildEnvironment(true);
        });
        assertThat(applicationContext.environment().isBuildEnvironment()).isTrue();
    }

    @Test
    @DisplayName("Customizer should be able to indicate non-build environment")
    void nonBuildEnvironmentCustomizer() {
        ApplicationContext applicationContext = createApplication(configuration -> {
            configuration.isBuildEnvironment(false);
        });
        assertThat(applicationContext.environment().isBuildEnvironment()).isFalse();
    }

    @Test
    @DisplayName("Customizer should be able to modify default bindings")
    void defaultBindingsCustomizer() {
        ApplicationContext applicationContext = createApplication(configuration -> {
            configuration.defaultBindings(bindings -> {
                bindings.bind(String.class).singleton("test");
            });
        });
        String value = applicationContext.get(String.class);
        assertThat(value).isEqualTo("test");
    }

    @ModuleActivator
    private @interface UseSampleActivator {
    }

    private static class SamplePreProcessor extends ComponentPreProcessor {

        @Override
        public <T> void process(
            InjectionCapableApplication application,
            ComponentProcessingContext<T> processingContext
        ) {
            // Do nothing
        }
    }

    private static class SamplePostProcessor extends ComponentPostProcessor {
        // Dummy post-processor for testing purposes
    }

    @Component
    private static class DummyUnmanagedComponent {
    }

    private static class HartshornBannerAccessor extends HartshornLogoBanner {
        @Override
        public Iterable<String> lines() {
            return super.lines();
        }
    }
}
