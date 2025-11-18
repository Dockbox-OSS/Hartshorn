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

package test.org.dockbox.hartshorn.inject.provider;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.ComponentResolutionException;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.inject.annotations.configuration.Configuration;
import org.dockbox.hartshorn.inject.annotations.configuration.Singleton;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.annotations.TestComponents;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.dockbox.hartshorn.util.ApplicationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import test.org.dockbox.hartshorn.inject.populate.PopulatedType;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

@HartshornIntegrationTest(includeBasePackages = false)
class ProviderBehaviorTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    void staticBindingCanBeProvided() {
        this.applicationContext.bind(SampleInterface.class).to(SampleImplementation.class);
        SampleInterface provided = this.applicationContext.get(SampleInterface.class);
        assertThat(provided).isNotNull();

        Class<? extends SampleInterface> providedClass = provided.getClass();
        assertThat(providedClass).isSameAs(SampleImplementation.class);

        assertThat(provided.name()).isEqualTo(SampleImplementation.NAME);
    }

    @Test
    void staticBindingWithMetaCanBeProvided() {
        ComponentKey<SampleInterface> key = ComponentKey.of(SampleInterface.class, "demo");
        this.applicationContext.bind(key).to(SampleImplementation.class);
        SampleInterface provided = this.applicationContext.get(key);
        assertThat(provided).isNotNull();

        Class<? extends SampleInterface> providedClass = provided.getClass();
        assertThat(providedClass).isSameAs(SampleImplementation.class);

        assertThat(provided.name()).isEqualTo(SampleImplementation.NAME);
    }

    @Test
    void instanceBindingCanBeProvided() {
        this.applicationContext.bind(SampleInterface.class).singleton(new SampleImplementation());
        SampleInterface provided = this.applicationContext.get(SampleInterface.class);
        assertThat(provided).isNotNull();

        Class<? extends SampleInterface> providedClass = provided.getClass();
        assertThat(providedClass).isSameAs(SampleImplementation.class);

        assertThat(provided.name()).isEqualTo(SampleImplementation.NAME);
    }

    @Test
    void instanceBindingWithMetaCanBeProvided() {
        ComponentKey<SampleInterface> key = ComponentKey.of(SampleInterface.class, "demo");
        this.applicationContext.bind(key).singleton(new SampleImplementation());
        SampleInterface provided = this.applicationContext.get(key);
        assertThat(provided).isNotNull();

        Class<? extends SampleInterface> providedClass = provided.getClass();
        assertThat(providedClass).isSameAs(SampleImplementation.class);

        assertThat(provided.name()).isEqualTo(SampleImplementation.NAME);
    }

    @Test
    void providerBindingCanBeProvided() {
        this.applicationContext.bind(SampleInterface.class).to(SampleImplementation::new);
        SampleInterface provided = this.applicationContext.get(SampleInterface.class);
        assertThat(provided).isNotNull();

        Class<? extends SampleInterface> providedClass = provided.getClass();
        assertThat(providedClass).isSameAs(SampleImplementation.class);

        assertThat(provided.name()).isEqualTo(SampleImplementation.NAME);
    }

    @Test
    void providerBindingWithMetaCanBeProvided() {
        ComponentKey<SampleInterface> key = ComponentKey.of(SampleInterface.class, "demo");
        this.applicationContext.bind(key).to(SampleImplementation::new);
        SampleInterface provided = this.applicationContext.get(key);
        assertThat(provided).isNotNull();

        Class<? extends SampleInterface> providedClass = provided.getClass();
        assertThat(providedClass).isSameAs(SampleImplementation.class);

        assertThat(provided.name()).isEqualTo(SampleImplementation.NAME);
    }

    @Test
    @TestComponents(SampleNamedConfiguration.class)
    void scannedMetaBindingsCanBeProvided() {

        // Ensure that the binding is not bound to the default name
        assertThatExceptionOfType(ComponentResolutionException.class).isThrownBy(() -> this.applicationContext.get(SampleInterface.class));

        SampleInterface provided = this.applicationContext.get(ComponentKey.of(SampleInterface.class, "meta"));
        assertThat(provided).isNotNull();

        Class<? extends SampleInterface> providedClass = provided.getClass();
        assertThat(providedClass).isSameAs(SampleMetaAnnotatedImplementation.class);

        assertThat(provided.name()).isEqualTo("MetaAnnotatedHartshorn");
    }

    @Test
    @TestComponents({PopulatedType.class, SampleConfiguration.class})
    void unboundTypesCanBeProvided() {
        PopulatedType provided = this.applicationContext.get(PopulatedType.class);
        assertThat(provided).isNotNull();
        assertThat(provided.sampleInterface()).isNotNull();
    }

    @Configuration
    public static class SampleConfiguration {

        @Singleton
        public SampleInterface sampleInterface() {
            return new SampleImplementation();
        }
    }

    @Test
    void failureInComponentConstructorYieldsInitializationException() {
        ComponentResolutionException exception = assertThatExceptionOfType(ComponentResolutionException.class).isThrownBy(() -> this.applicationContext.get(
                ErrorInConstructorObject.class)).actual();
        Throwable cause = exception.getCause();
        assertThat(cause).isNotNull();
        assertThat(cause).isInstanceOf(ApplicationException.class);

        ApplicationException applicationException = (ApplicationException) cause;
        assertThat(applicationException.getMessage()).isEqualTo("Failed to create instance of type " + ErrorInConstructorObject.class.getName());
    }

    @Test
    void failingConstructorIsRethrown() {
        ComponentResolutionException exception = assertThatExceptionOfType(ComponentResolutionException.class).isThrownBy(() -> this.applicationContext.get(
                TypeWithFailingConstructor.class)).actual();
        assertThat(exception.getCause()).isInstanceOf(ApplicationException.class);

        ApplicationException applicationException = (ApplicationException) exception.getCause();
        assertThat(applicationException.getCause()).isInstanceOf(IllegalStateException.class);

        IllegalStateException illegalStateException = (IllegalStateException) applicationException.getCause();
        assertThat(illegalStateException.getMessage()).isEqualTo(TypeWithFailingConstructor.ERROR_MESSAGE);
    }

    @Test
    void stringProvision() {
        ComponentKey<String> key = ComponentKey.of(String.class, "license");
        this.applicationContext.bind(key).singleton("MIT");
        String license = this.applicationContext.get(key);
        assertThat(license).isEqualTo("MIT");
    }

    @ParameterizedTest
    @MethodSource("providers")
    @TestComponents({ SampleFieldImplementation.class, SampleProviderConfiguration.class})
    void providersCanApply(String meta, String name, boolean field, String fieldMeta, boolean singleton) {
        if (field) {
            if (fieldMeta == null) {this.applicationContext.bind(SampleField.class).to(SampleFieldImplementation.class);}
            else {
                this.applicationContext.bind(ComponentKey.of(SampleField.class, fieldMeta)).to(SampleFieldImplementation.class);
            }
        }

        ProvidedInterface provided;
        if (meta == null) {
            provided = this.applicationContext.get(ProvidedInterface.class);
        }
        else {
            provided = this.applicationContext.get(ComponentKey.of(ProvidedInterface.class, meta));
        }
        assertThat(provided).isNotNull();

        String actual = provided.name();
        assertThat(name).isNotNull();
        assertThat(actual).isEqualTo(name);

        if (singleton) {
            ProvidedInterface second;
            if (meta == null) {
                second = this.applicationContext.get(ProvidedInterface.class);
            }
            else {
                second = this.applicationContext.get(ComponentKey.of(ProvidedInterface.class, meta));
            }
            assertThat(second).isNotNull();
            assertThat(second).isSameAs(provided);
        }
    }

    private static Stream<Arguments> providers() {
        return Stream.of(
                Arguments.of(null, "Provision", false, null, false),
                Arguments.of("named", "NamedProvision", false, null, false),
                Arguments.of("parameter", "ParameterProvision", true, null, false),
                Arguments.of("namedParameter", "NamedParameterProvision", true, "named", false),
                Arguments.of("singleton", "SingletonProvision", false, null, true)
        );
    }
}
