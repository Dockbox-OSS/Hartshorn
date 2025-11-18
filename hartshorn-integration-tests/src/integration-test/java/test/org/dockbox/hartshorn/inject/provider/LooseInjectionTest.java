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

import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.HartshornApplication;
import org.dockbox.hartshorn.launchpad.launch.StandardApplicationContextFactory;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.launchpad.environment.ConfigurableApplicationEnvironment;
import org.dockbox.hartshorn.launchpad.environment.ConfigurableApplicationEnvironment.Configurer;
import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.ComponentResolutionException;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.dockbox.hartshorn.util.Tristate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

@HartshornIntegrationTest(includeBasePackages = false)
public class LooseInjectionTest {

    @Test
    void nonStrictModeMatchesCompatibleBinding(@Inject ApplicationContext context) {
        context.bind(String.class).singleton("Hello World");
        ComponentKey<CharSequence> key = ComponentKey.builder(CharSequence.class)
                .strict(false)
                .build();
        CharSequence sequence = context.get(key);
        assertThat(sequence).isEqualTo("Hello World");
    }

    @Test
    void strictModeOnlyMatchesExactBinding(@Inject ApplicationContext context) {
        context.bind(String.class).singleton("Hello World");
        ComponentKey<CharSequence> key = ComponentKey.builder(CharSequence.class)
                .strict(true)
                .build();
        assertThatExceptionOfType(ComponentResolutionException.class).isThrownBy(() -> context.get(key));
    }

    @Test
    void strictModeIsUndefinedByDefault() {
        ComponentKey<CharSequence> componentKey = ComponentKey.of(CharSequence.class);
        assertThat(componentKey.strict()).isSameAs(Tristate.UNDEFINED);
    }

    @Test
    void environmentStrictModeIsEnabledByDefault() {
        ApplicationEnvironment environment = HartshornApplication.create(LooseInjectionTest.class, application -> {
            application.applicationContextFactory(StandardApplicationContextFactory.create(constructor -> {
                constructor.includeBasePackages(false);
            }));
        }).environment();
        assertThat(environment.isStrictMode()).isTrue();
    }

    public static void main(String[] args) {
        HartshornApplication.create(LooseInjectionTest.class, application -> {
            application.applicationContextFactory(StandardApplicationContextFactory.create(constructor -> {
                constructor.includeBasePackages(false);
            }));
        });
    }


    @Test
    void customizingEnvironmentStrictModeAffectsLookup() {
        ApplicationContext applicationContext = HartshornApplication.create(LooseInjectionTest.class, application -> {
            application.applicationContextFactory(StandardApplicationContextFactory.create(constructor -> {
                constructor.includeBasePackages(false);
                constructor.environment(ConfigurableApplicationEnvironment.create(Configurer::disableStrictMode));
            }));
        });
        ApplicationEnvironment environment = applicationContext.environment();
        assertThat(environment.isStrictMode()).isFalse();

        applicationContext.bind(String.class).singleton("Hello World");
        ComponentKey<CharSequence> key = ComponentKey.builder(CharSequence.class).build();
        assertThat(key.strict()).isSameAs(Tristate.UNDEFINED);

        CharSequence sequence = applicationContext.get(key);
        assertThat(sequence).isEqualTo("Hello World");
    }
}
