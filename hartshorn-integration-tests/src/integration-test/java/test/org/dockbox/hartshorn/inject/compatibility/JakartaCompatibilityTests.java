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

import jakarta.annotation.Resource;
import jakarta.inject.Inject;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@HartshornIntegrationTest(
        includeBasePackages = false,
        customizers = DisableSingleConstructorFallbackTestCustomizer.class
)
public class JakartaCompatibilityTests {

    /**
     * Test application customizer that enables support for Jakarta annotations in the test application. Note that
     * typically Jakarta support would be enabled through {@link HartshornApplicationConfigurer#withJakartaAnnotations()}.
     */
    public static class EnableJakartaTestApplicationCustomizer implements TestApplicationCustomizer {
        @Override
        public void customizeApplication(SimpleApplicationContext.Configurer configurer) {
            configurer.componentProvider(HierarchicalComponentProviderOrchestrator.create(orchestrator -> {
                orchestrator.componentPostConstructor(AnnotatedMethodComponentPostConstructor.create(
                        AnnotatedMethodComponentPostConstructor.Configurer::withJakartaAnnotations
                ));
            }));
        }

        @Override
        public void customizeEnvironment(ConfigurableApplicationEnvironment.Configurer configurer) {
            configurer.injectionPointsResolver(ContextualInitializer.defer(() ->
                    MethodsAndFieldsInjectionPointResolver.create(
                            MethodsAndFieldsInjectionPointResolver.Configurer::withJakartaAnnotations
                    )
            ));
        }
    }

    // Hartshorn annotation, as Jakarta is disabled for several tests
    @org.dockbox.hartshorn.inject.annotations.Inject
    private ApplicationContext applicationContext;

    @Test
    @HartshornIntegrationTest(customizers = EnableJakartaTestApplicationCustomizer.class)
    @DisplayName("Jakarta annotations are supported for field injection, if enabled")
    void testJakartaFieldInjectSupportedIfEnabled() {
        this.applicationContext.defaultBinder().bind(String.class).singleton("Hello, World!");

        FieldJakartaComponent component = this.applicationContext.defaultProvider().get(FieldJakartaComponent.class);
        Assertions.assertNotNull(component);

        Assertions.assertEquals("Hello, World!", component.messageAsInject);
        Assertions.assertEquals("Hello, World!", component.messageAsResource);
    }

    @Test
    @DisplayName("Jakarta annotations are not supported for field injection, if disabled")
    void testJakartaFieldInjectFailsIfDisabled() {
        this.applicationContext.defaultBinder().bind(String.class).singleton("Hello, World!");

        FieldJakartaComponent component = this.applicationContext.defaultProvider().get(FieldJakartaComponent.class);
        Assertions.assertNotNull(component);

        Assertions.assertNull(component.messageAsInject, "Field injection with @jakarta.inject.Inject should not be supported when Jakarta compatibility is disabled");
        Assertions.assertNull(component.messageAsResource, "Field injection with @jakarta.annotation.Resource should not be supported when Jakarta compatibility is disabled");
    }

    @Test
    @HartshornIntegrationTest(customizers = EnableJakartaTestApplicationCustomizer.class)
    @DisplayName("Jakarta annotations are supported for constructor injection, if enabled")
    void testJakartaConstructorInjectSupportedIfEnabled() {
        this.applicationContext.defaultBinder().bind(String.class).singleton("Hello, World!");

        ConstructorJakartaComponent component = this.applicationContext.defaultProvider().get(ConstructorJakartaComponent.class);
        Assertions.assertNotNull(component);

        Assertions.assertEquals("Hello, World!", component.messageAsInject);
    }

    @Test
    @DisplayName("Jakarta annotations are not supported for constructor injection, if disabled")
    void testJakartaConstructorInjectFailsIfDisabled() {
        this.applicationContext.defaultBinder().bind(String.class).singleton("Hello, World!");

        // No compatible constructor should be found, as Jakarta compatibility is disabled
        ComponentResolutionException componentResolutionException = Assertions.assertThrows(
                ComponentResolutionException.class,
                () -> this.applicationContext.defaultProvider().get(ConstructorJakartaComponent.class),
                "Constructor injection with @jakarta.inject.Inject should not be supported when Jakarta compatibility is disabled"
        );
        Assertions.assertInstanceOf(
                MissingInjectConstructorException.class,
                componentResolutionException.getCause(),
                "Constructor injection with @jakarta.inject.Inject should not be supported when Jakarta compatibility is disabled"
        );
    }

    @Test
    @HartshornIntegrationTest(customizers = EnableJakartaTestApplicationCustomizer.class)
    @DisplayName("Jakarta annotations are supported for method injection, if enabled")
    void testJakartaMethodInjectSupportedIfEnabled() {
        this.applicationContext.defaultBinder().bind(String.class).singleton("Hello, World!");

        MethodJakartaComponent component = this.applicationContext.defaultProvider().get(MethodJakartaComponent.class);
        Assertions.assertNotNull(component);

        Assertions.assertEquals("Hello, World!", component.messageAsInject);
        Assertions.assertEquals("Hello, World!", component.messageAsResource);
    }

    @Test
    @DisplayName("Jakarta annotations are not supported for method injection, if disabled")
    void testJakartaMethodInjectFailsIfDisabled() {
        this.applicationContext.defaultBinder().bind(String.class).singleton("Hello, World!");

        MethodJakartaComponent component = this.applicationContext.defaultProvider().get(MethodJakartaComponent.class);
        Assertions.assertNotNull(component);

        Assertions.assertNull(component.messageAsInject, "Method injection with @jakarta.inject.Inject should not be supported when Jakarta compatibility is disabled");
        Assertions.assertNull(component.messageAsResource, "Method injection with @jakarta.annotation.Resource should not be supported when Jakarta compatibility is disabled");
    }

    private static class FieldJakartaComponent {
        @Inject
        String messageAsInject;

        @Resource
        String messageAsResource;
    }

    private static class ConstructorJakartaComponent {
        final String messageAsInject;

        @Inject
        public ConstructorJakartaComponent(String messageAsInject) {
            this.messageAsInject = messageAsInject;
        }
    }

    private static class MethodJakartaComponent {
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
