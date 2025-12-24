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

package test.org.dockbox.hartshorn.inject.compatibility;

import org.dockbox.hartshorn.inject.ComponentResolutionException;
import org.dockbox.hartshorn.inject.processing.construction.AnnotatedMethodComponentPostConstructor;
import org.dockbox.hartshorn.inject.provider.HierarchicalComponentProviderOrchestrator;
import org.dockbox.hartshorn.inject.provider.MissingInjectConstructorException;
import org.dockbox.hartshorn.inject.targets.MethodsAndFieldsInjectionPointResolver;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.HartshornApplicationConfigurer;
import org.dockbox.hartshorn.launchpad.SimpleApplicationContext;
import org.dockbox.hartshorn.launchpad.environment.ConfigurableApplicationEnvironment;
import org.dockbox.hartshorn.test.TestApplicationCustomizer;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.dockbox.hartshorn.util.configure.ContextualInitializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;
import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@HartshornIntegrationTest(
    includeBasePackages = false,
    customizers = DisableSingleConstructorFallbackTestCustomizer.class
)
class JavaxCompatibilityTests {

    /**
     * Test application customizer that enables support for Javax annotations in the test
     * application. Note that typically Javax support would be enabled through
     * {@link HartshornApplicationConfigurer#withJavaxAnnotations()}.
     */
    public static class EnableJavaxTestApplicationCustomizer implements TestApplicationCustomizer {
        @Override
        public void customizeApplication(SimpleApplicationContext.Configurer configurer) {
            configurer.componentProvider(HierarchicalComponentProviderOrchestrator.create(
                orchestrator -> orchestrator.componentPostConstructor(
                        AnnotatedMethodComponentPostConstructor.create(
                                AnnotatedMethodComponentPostConstructor.Configurer
                                        ::withJavaxAnnotations
                        )
                )));
        }

        @Override
        public void customizeEnvironment(ConfigurableApplicationEnvironment.Configurer configurer) {
            configurer.injectionPointsResolver(ContextualInitializer.defer(() ->
                MethodsAndFieldsInjectionPointResolver.create(
                    MethodsAndFieldsInjectionPointResolver.Configurer::withJavaxAnnotations
                )
            ));
        }
    }

    // Hartshorn annotation, as Jakarta is disabled for several tests
    @org.dockbox.hartshorn.inject.annotations.Inject
    private ApplicationContext applicationContext;

    @Test
    @HartshornIntegrationTest(customizers = EnableJavaxTestApplicationCustomizer.class)
    @DisplayName("Javax annotations are supported for field injection, if enabled")
    void javaxFieldInjectSupportedIfEnabled() {
        this.applicationContext.defaultBinder().bind(String.class).singleton("Hello, World!");

        FieldJavaxComponent component =
            this.applicationContext.defaultProvider().get(FieldJavaxComponent.class);
        assertThat(component).isNotNull();

        assertThat(component.messageAsInject).isEqualTo("Hello, World!");
        assertThat(component.messageAsResource).isEqualTo("Hello, World!");
    }

    @Test
    @DisplayName("Javax annotations are not supported for field injection, if disabled")
    void javaxFieldInjectFailsIfDisabled() {
        this.applicationContext.defaultBinder().bind(String.class).singleton("Hello, World!");

        FieldJavaxComponent component =
            this.applicationContext.defaultProvider().get(FieldJavaxComponent.class);
        assertThat(component).isNotNull();

        assertThat(component.messageAsInject).isNull();
        assertThat(component.messageAsResource).isNull();
    }

    @Test
    @HartshornIntegrationTest(customizers = EnableJavaxTestApplicationCustomizer.class)
    @DisplayName("Javax annotations are supported for constructor injection, if enabled")
    void javaxConstructorInjectSupportedIfEnabled() {
        this.applicationContext.defaultBinder().bind(String.class).singleton("Hello, World!");

        ConstructorJavaxComponent component =
            this.applicationContext.defaultProvider().get(ConstructorJavaxComponent.class);
        assertThat(component).isNotNull();

        assertThat(component.messageAsInject).isEqualTo("Hello, World!");
    }

    @Test
    @DisplayName("Javax annotations are not supported for constructor injection, if disabled")
    void javaxConstructorInjectFailsIfDisabled() {
        this.applicationContext.defaultBinder().bind(String.class).singleton("Hello, World!");

        // No compatible constructor should be found, as Javax compatibility is disabled
        Throwable componentResolutionException = assertThatThrownBy(() -> {
            this.applicationContext.defaultProvider()
                .get(JavaxCompatibilityTests.ConstructorJavaxComponent.class);
        }).isInstanceOf(ComponentResolutionException.class).actual();
        assertThat(componentResolutionException.getCause())
                .isInstanceOf(MissingInjectConstructorException.class);
    }

    @Test
    @HartshornIntegrationTest(customizers = EnableJavaxTestApplicationCustomizer.class)
    @DisplayName("Javax annotations are supported for method injection, if enabled")
    void javaxMethodInjectSupportedIfEnabled() {
        this.applicationContext.defaultBinder().bind(String.class).singleton("Hello, World!");

        MethodJavaxComponent component =
            this.applicationContext.defaultProvider().get(MethodJavaxComponent.class);
        assertThat(component).isNotNull();

        assertThat(component.messageAsInject).isEqualTo("Hello, World!");
        assertThat(component.messageAsResource).isEqualTo("Hello, World!");
    }

    @Test
    @DisplayName("Javax annotations are not supported for method injection, if disabled")
    void javaxMethodInjectFailsIfDisabled() {
        this.applicationContext.defaultBinder().bind(String.class).singleton("Hello, World!");

        MethodJavaxComponent component =
            this.applicationContext.defaultProvider().get(MethodJavaxComponent.class);
        assertThat(component).isNotNull();

        assertThat(component.messageAsInject).isNull();
        assertThat(component.messageAsResource).isNull();
    }

    private static class FieldJavaxComponent {
        @Inject
        String messageAsInject;

        @Resource
        String messageAsResource;
    }

    private static class ConstructorJavaxComponent {
        final String messageAsInject;

        @Inject
        public ConstructorJavaxComponent(String messageAsInject) {
            this.messageAsInject = messageAsInject;
        }
    }

    private static class MethodJavaxComponent {
        String messageAsInject;
        String messageAsResource;

        @Inject
        public void setMessageAsInject(String messageAsInject) {
            this.messageAsInject = messageAsInject;
        }

        @Resource
        public void setMessageAsResource(String messageAsResource) {
            this.messageAsResource = messageAsResource;
        }
    }
}
