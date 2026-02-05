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

package test.org.dockbox.hartshorn.inject.provider;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.ComponentResolutionException;
import org.dockbox.hartshorn.inject.IllegalScopeException;
import org.dockbox.hartshorn.inject.ImmutableInjectorConfiguration;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.inject.collection.ComponentCollection;
import org.dockbox.hartshorn.inject.scope.DirectScopeKey;
import org.dockbox.hartshorn.inject.scope.Scope;
import org.dockbox.hartshorn.inject.scope.ScopeKey;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.HartshornApplication;
import org.dockbox.hartshorn.launchpad.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.launchpad.environment.ConfigurableApplicationEnvironment;
import org.dockbox.hartshorn.launchpad.launch.StandardApplicationContextFactory;
import org.dockbox.hartshorn.test.TestApplicationCustomizer;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.dockbox.hartshorn.util.Tristate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

@HartshornIntegrationTest(includeBasePackages = false)
public class FuzzyInjectionTest {

    @Test
    void fuzzyModeMatchesCompatibleBinding(@Inject ApplicationContext context) {
        context.bind(String.class).singleton("Hello World");
        ComponentKey<CharSequence> key = ComponentKey.builder(CharSequence.class)
                .fuzzy()
                .build();
        CharSequence sequence = context.get(key);
        assertThat(sequence).isEqualTo("Hello World");
    }

    @Test
    void strictModeOnlyMatchesExactBinding(@Inject ApplicationContext context) {
        context.bind(String.class).singleton("Hello World");
        ComponentKey<CharSequence> key = ComponentKey.builder(CharSequence.class)
                .strict()
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
        ApplicationEnvironment environment = HartshornApplication.create(FuzzyInjectionTest.class, application -> {
            application.applicationContextFactory(StandardApplicationContextFactory.create(constructor -> {
                constructor.includeBasePackages(false);
            }));
        }).environment();
        assertThat(environment.configuration().isStrictMode()).isTrue();
    }

    @Test
    void customizingEnvironmentStrictModeAffectsLookup() {
        // Could usually be simplified by calling HartshornApplication#createApplication, but as we
        // need to disable base package scanning, we have to go through the 'full' factory setup.
        ApplicationContext applicationContext = HartshornApplication.create(FuzzyInjectionTest.class, application -> {
            application.applicationContextFactory(StandardApplicationContextFactory.create(constructor -> {
                constructor.includeBasePackages(false);
                constructor.environment(ConfigurableApplicationEnvironment.create(environment -> {
                    environment.injectorConfiguration(ImmutableInjectorConfiguration.create(
                        ImmutableInjectorConfiguration.Configurer::disableStrictMode
                    ));
                }));
            }));
        });
        ApplicationEnvironment environment = applicationContext.environment();
        assertThat(environment.configuration().isStrictMode()).isFalse();

        applicationContext.bind(String.class).singleton("Hello World");
        ComponentKey<CharSequence> key = ComponentKey.builder(CharSequence.class).build();
        assertThat(key.strict()).isSameAs(Tristate.UNDEFINED);

        CharSequence sequence = applicationContext.get(key);
        assertThat(sequence).isEqualTo("Hello World");
    }

    @Test
    void fuzzyMatchingGlobalScopeShouldIncludeGlobalMembers(
        @Inject ApplicationContext applicationContext
    ) throws IllegalScopeException {
        configureValuesInParentAndChildScope(applicationContext);

        ComponentCollection<String> strings = applicationContext.get(
            ComponentKey.collect(String.class)
        );
        assertThat(strings)
            .hasSize(1)
            .anySatisfy(s -> assertThat(s).isEqualTo(
                applicationContext.scope().installableScopeType().name()
            ));

        ComponentCollection<CharSequence> charSequences = applicationContext.get(
            ComponentKey.builder(CharSequence.class)
                .collector()
                .fuzzy()
                .build()
        );
        assertThat(charSequences)
            .hasSize(1)
            .anySatisfy(s -> assertThat(s).isEqualTo(
                applicationContext.scope().installableScopeType().name()
            ));
    }

    @Test
    @HartshornIntegrationTest(customizers = EnableIncludeParentScopeMembers.class)
    void enablingFuzzyMatchingParentScopeMembersShouldIncludeGlobalMembers(
        @Inject ApplicationContext applicationContext
    ) throws IllegalScopeException {
        configureValuesInParentAndChildScope(applicationContext);

        // Should still match on global scope
        ComponentCollection<String> strings = applicationContext.get(
            ComponentKey.collect(String.class)
        );
        assertThat(strings)
            .hasSize(1)
            .anySatisfy(s -> assertThat(s).isEqualTo(
                applicationContext.scope().installableScopeType().name()
            ));

        ComponentCollection<CharSequence> stringsInChild =
            applicationContext.get(ComponentKey.builder(CharSequence.class)
                .scope(new SampleScope())
                .collector()
                .fuzzy()
                .build());
        assertThat(stringsInChild).isEmpty();
    }

    @Test
    @HartshornIntegrationTest(customizers = DisableIncludeParentScopeMembers.class)
    void disablingFuzzyMatchingParentScopeMembersShouldExcludeGlobalMembers(
        @Inject ApplicationContext applicationContext
    ) throws IllegalScopeException {
        configureValuesInParentAndChildScope(applicationContext);
        // Should still match on global scope
        ComponentCollection<String> strings = applicationContext.get(
            ComponentKey.collect(String.class)
        );
        assertThat(strings)
            .hasSize(1)
            .anySatisfy(s -> assertThat(s).isEqualTo(
                applicationContext.scope().installableScopeType().name()
            ));

        // Should not match on child scope, as parent scope members are excluded from fuzzy matching
        ComponentCollection<CharSequence> stringsInChild =
            applicationContext.get(ComponentKey.builder(CharSequence.class)
                .scope(new SampleScope())
                .collector()
                .fuzzy()
                .build());
        assertThat(stringsInChild)
            .hasSize(1)
            .anySatisfy(s -> assertThat(s).isEqualTo(
                SampleScope.class.getName()
            ));
    }

    public static class SampleScope implements Scope {

        @Override
        public ScopeKey installableScopeType() {
            return DirectScopeKey.of(SampleScope.class);
        }
    }

    public static class EnableIncludeParentScopeMembers implements TestApplicationCustomizer {
        @Override
        public void customizeInjector(ImmutableInjectorConfiguration.Configurer configurer) {
            configurer.includeParentScopeForFuzzyMatching();
        }
    }

    public static class DisableIncludeParentScopeMembers implements TestApplicationCustomizer {
        @Override
        public void customizeInjector(ImmutableInjectorConfiguration.Configurer configurer) {
            configurer.excludeParentScopeForFuzzyMatching();
        }
    }

    private static void configureValuesInParentAndChildScope(
        ApplicationContext applicationContext
    ) throws IllegalScopeException {
        applicationContext.bind(String.class)
                .collect(collector -> {
                    collector.singleton(ApplicationContext.class.getName());
                });

        // TODO #1167: Issue with scoped bindings and collectors, they're registered
        //  to the application scope? Should not happen.
        applicationContext.bind(String.class)
            .installTo(DirectScopeKey.of(SampleScope.class))
            .collect(collector -> {
                collector.singleton(SampleScope.class.getName());
            });
    }
}
