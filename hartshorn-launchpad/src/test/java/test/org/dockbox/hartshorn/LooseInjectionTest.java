/*
 * Copyright 2019-2024 the original author or authors.
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

package test.org.dockbox.hartshorn;

import org.dockbox.hartshorn.application.HartshornApplication;
import org.dockbox.hartshorn.application.StandardApplicationContextFactory;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.application.environment.ContextualApplicationEnvironment;
import org.dockbox.hartshorn.application.environment.ContextualApplicationEnvironment.Configurer;
import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.ComponentResolutionException;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.dockbox.hartshorn.util.Tristate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@HartshornTest(includeBasePackages = false)
public class LooseInjectionTest {

    @InjectTest
    void testNonStrictModeMatchesCompatibleBinding(ApplicationContext context) {
        context.bind(String.class).singleton("Hello World");
        ComponentKey<CharSequence> key = ComponentKey.builder(CharSequence.class)
                .strict(false)
                .build();
        CharSequence sequence = context.get(key);
        Assertions.assertEquals("Hello World", sequence);
    }

    @InjectTest
    void testStrictModeOnlyMatchesExactBinding(ApplicationContext context) {
        context.bind(String.class).singleton("Hello World");
        ComponentKey<CharSequence> key = ComponentKey.builder(CharSequence.class)
                .strict(true)
                .build();
        Assertions.assertThrows(ComponentResolutionException.class, () -> context.get(key));
    }

    @Test
    void testStrictModeIsUndefinedByDefault() {
        ComponentKey<CharSequence> componentKey = ComponentKey.of(CharSequence.class);
        Assertions.assertSame(Tristate.UNDEFINED, componentKey.strict());
    }

    @Test
    void testEnvironmentStrictModeIsEnabledByDefault() {
        ApplicationEnvironment environment = HartshornApplication.create(LooseInjectionTest.class, application -> {
            application.applicationContextFactory(StandardApplicationContextFactory.create(constructor -> {
                constructor.includeBasePackages(false);
            }));
        }).environment();
        Assertions.assertTrue(environment.isStrictMode());
    }

    public static void main(String[] args) {
        HartshornApplication.create(LooseInjectionTest.class, application -> {
            application.applicationContextFactory(StandardApplicationContextFactory.create(constructor -> {
                constructor.includeBasePackages(false);
            }));
        });
    }


    @Test
    void testCustomizingEnvironmentStrictModeAffectsLookup() {
        ApplicationContext applicationContext = HartshornApplication.create(LooseInjectionTest.class, application -> {
            application.applicationContextFactory(StandardApplicationContextFactory.create(constructor -> {
                constructor.includeBasePackages(false);
                constructor.environment(ContextualApplicationEnvironment.create(Configurer::disableStrictMode));
            }));
        });
        ApplicationEnvironment environment = applicationContext.environment();
        Assertions.assertFalse(environment.isStrictMode());

        applicationContext.bind(String.class).singleton("Hello World");
        ComponentKey<CharSequence> key = ComponentKey.builder(CharSequence.class).build();
        Assertions.assertSame(Tristate.UNDEFINED, key.strict());

        CharSequence sequence = applicationContext.get(key);
        Assertions.assertEquals("Hello World", sequence);
    }
}
