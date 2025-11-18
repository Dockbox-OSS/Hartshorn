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

package test.org.dockbox.hartshorn.inject.scope.provider;

import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.annotations.Component;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.inject.annotations.configuration.Configuration;
import org.dockbox.hartshorn.inject.annotations.configuration.Scoped;
import org.dockbox.hartshorn.inject.annotations.configuration.Singleton;
import org.dockbox.hartshorn.inject.scope.DirectScopeKey;
import org.dockbox.hartshorn.inject.scope.Scope;
import org.dockbox.hartshorn.inject.scope.ScopeKey;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.annotations.TestComponents;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@HartshornIntegrationTest(includeBasePackages = false)
@TestComponents({
        ScopedInjectionTests.ScopedConfiguration.class,
        ScopedInjectionTests.TestManagedComponent.class,
})
class ScopedInjectionTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("Scoped requests of unmanaged components should have dependencies from the same scope")
    void scopedRequestOfUnmanagedComponentHasScopedDependencies() {
        TestScope scope = new TestScope();

        ComponentKey<String> scopedStringKey = ComponentKey.builder(String.class).scope(scope).build();
        assertThat(this.applicationContext.get(scopedStringKey)).isEqualTo("testScopedString");

        TestComponent component = this.applicationContext.get(ComponentKey.builder(TestComponent.class)
                .scope(scope)
                .build());
        assertThat(component).isNotNull();

        // Injected String should inherit the scope from the component
        assertThat(component.scopedString).isEqualTo("testScopedString");
    }

    @Test
    @DisplayName("Unscoped requests of components should have application dependencies")
    void unscopedRequestOfUnmanagedComponentHasApplicationDependencies() {
        ComponentKey<String> applicationStringKey = ComponentKey.of(String.class);
        assertThat(this.applicationContext.get(applicationStringKey)).isEqualTo("applicationScopedString");

        TestComponent component = this.applicationContext.get(TestComponent.class);
        assertThat(component).isNotNull();

        // Injected String should be the application scoped value
        assertThat(component.scopedString).isEqualTo("applicationScopedString");
    }

    @Test
    @DisplayName("Scoped requests of components with context dependencies should have the scoped context")
    void scopedRequestOfUnmanagedComponentWithScopeContextHasScopedContext() {
        TestScope scope = new TestScope();
        scope.addContext(new SampleContext("test"));
        this.applicationContext.addContext(new SampleContext("application"));

        ComponentKey<TestComponent> scopedComponentKey = ComponentKey.builder(TestComponent.class)
                .scope(scope)
                .build();
        TestComponent component = this.applicationContext.get(scopedComponentKey);
        assertThat(component).isNotNull();

        // Injected context should be the scoped context
        assertThat(component.context).isNotNull();
        assertThat(component.context.value).isEqualTo("test");
    }

    @Test
    @DisplayName("Scoped requests of components with context dependencies which are absent in the scope context should have global context")
    void scopedRequestOfUnmanagedComponentWithAbsentContextHasGlobalContext() {
        TestScope scope = new TestScope();
        this.applicationContext.addContext(new SampleContext("application"));

        ComponentKey<TestComponent> scopedComponentKey = ComponentKey.builder(TestComponent.class)
                .scope(scope)
                .build();
        TestComponent component = this.applicationContext.get(scopedComponentKey);
        assertThat(component).isNotNull();

        // Injected context should be the application context, as the context is not present in the scope
        assertThat(component.context).isNotNull();
        assertThat(component.context.value).isEqualTo("application");
    }

    @Test
    @DisplayName("Managed components should always be application scoped, even if the request is scoped")
    void scopedRequestOfManagedComponentIsApplicationRedirect() {
        TestScope scope = new TestScope();

        ComponentKey<TestManagedComponent> scopedManagedComponentKey = ComponentKey.builder(TestManagedComponent.class)
                .scope(scope)
                .build();
        TestManagedComponent scopedManagedComponent = this.applicationContext.get(scopedManagedComponentKey);
        assertThat(scopedManagedComponent).isNotNull();

        // Managed components should always be application scoped, even if the request for it is scoped
        TestManagedComponent applicationManagedComponent = this.applicationContext.get(TestManagedComponent.class);
        assertThat(scopedManagedComponent).isSameAs(applicationManagedComponent);

        // The dependency should be the application scoped value
        assertThat(scopedManagedComponent.scopedString).isEqualTo("applicationScopedString");
    }

    @Test
    @DisplayName("Scoped requests with the same scope type but different scope instances should yield different components")
    void scopedRequestOfDifferentContextWithSameTypeIsDifferentComponent() {
        TestScope scope1 = new TestScope();
        scope1.addContext(new SampleContext("test1"));
        TestScope scope2 = new TestScope();
        scope2.addContext(new SampleContext("test2"));

        ComponentKey<TestComponent> scopedComponentKey1 = ComponentKey.builder(TestComponent.class).scope(scope1).build();
        ComponentKey<TestComponent> scopedComponentKey2 = ComponentKey.builder(TestComponent.class).scope(scope2).build();

        TestComponent scopedComponent1 = this.applicationContext.get(scopedComponentKey1);
        TestComponent scopedComponent2 = this.applicationContext.get(scopedComponentKey2);

        assertThat(scopedComponent1).isNotNull();
        assertThat(scopedComponent2)
                // Each scoped component should have its own instance, even if they are of the same type
                .isNotSameAs(scopedComponent1);

        // Each component should have its own context
        assertThat(scopedComponent1.context).isNotNull();
        assertThat(scopedComponent2.context).isNotNull();

        assertThat(scopedComponent1.context.value).isEqualTo("test1");
        assertThat(scopedComponent2.context.value).isEqualTo("test2");
    }

    public static class TestComponent {
        @Inject
        private String scopedString;

        @Inject
        private SampleContext context;
    }

    @Component
    public static class TestManagedComponent {
        @Inject
        private String scopedString;
    }

    @Configuration
    public static class ScopedConfiguration {

        @Singleton
        @Scoped(TestScope.class)
        public String testScopedString() {
            return "testScopedString";
        }

        @Singleton
        public String applicationScopedString() {
            return "applicationScopedString";
        }
    }

    public static class TestScope extends DefaultContext implements Scope {

        @Override
        public ScopeKey installableScopeType() {
            return DirectScopeKey.of(TestScope.class);
        }
    }

    public static class SampleContext extends DefaultContext {
        private final String value;

        public SampleContext(String value) {
            this.value = value;
        }
    }
}
