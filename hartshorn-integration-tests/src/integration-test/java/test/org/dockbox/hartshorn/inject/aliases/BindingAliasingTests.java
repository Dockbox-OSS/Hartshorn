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

package test.org.dockbox.hartshorn.inject.aliases;

import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.inject.annotations.configuration.BindingAlias;
import org.dockbox.hartshorn.inject.annotations.configuration.Configuration;
import org.dockbox.hartshorn.inject.annotations.configuration.Singleton;
import org.dockbox.hartshorn.inject.binding.AmbiguousComponentException;
import org.dockbox.hartshorn.inject.provider.ComponentProvider;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.annotations.TestComponents;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

@HartshornIntegrationTest(includeBasePackages = false)
class BindingAliasingTests {

    @Test
    void aliasBindingsCanBeResolved(@Inject ApplicationContext applicationContext) {
        applicationContext.bind(String.class)
                .alias(CharSequence.class)
                .singleton("Hello world");

        String helloWorldString = applicationContext.get(String.class);
        CharSequence helloWorldCharSequence = applicationContext.get(CharSequence.class);

        assertThat(helloWorldCharSequence).isSameAs(helloWorldString);
    }

    @Test
    @TestComponents(BindingAliasingConfiguration.class)
    void aliasBindingsFromConfigurationCanBeResolved(@Inject ComponentProvider provider) {
        String helloWorldString = provider.get(String.class);
        CharSequence helloWorldCharSequence = provider.get(CharSequence.class);

        assertThat(helloWorldCharSequence).isSameAs(helloWorldString);
    }

    @Test
    @TestComponents(OverlappingDefaultAndAliasBindingConfiguration.class)
    void overlappingDefaultAndAliasBindingsFromConfigurationCanResolve(@Inject ComponentProvider provider) {
        assertThatCode(() -> provider.get(String.class)).doesNotThrowAnyException();
        assertThatCode(() -> provider.get(CharSequence.class)).doesNotThrowAnyException();
    }

    @Test
    void overlappingPriorityAliasBindingsFails(@Inject ApplicationContext applicationContext) {
        applicationContext.bind(String.class)
                .alias(CharSequence.class)
                .priority(1)
                .singleton("Hello world");
        applicationContext.bind(StringBuilder.class)
                .alias(CharSequence.class)
                .priority(1)
                .singleton(new StringBuilder("Hello world"));

        assertThatExceptionOfType(AmbiguousComponentException.class).isThrownBy(() -> applicationContext.get(CharSequence.class));
    }

    @Configuration
    public static class BindingAliasingConfiguration {

        @Singleton
        @BindingAlias(CharSequence.class)
        public String helloWorld() {
            return "Hello world";
        }
    }

    @Configuration
    public static class OverlappingDefaultAndAliasBindingConfiguration {

        @Singleton
        @BindingAlias(CharSequence.class)
        public String helloWorld() {
            return "Hello world";
        }

        @Singleton
        public CharSequence helloWorldSequence() {
            return "Hello world sequence";
        }
    }
}
