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

package test.org.dockbox.hartshorn.inject.scope;

import org.dockbox.hartshorn.inject.scope.Scope;
import org.dockbox.hartshorn.inject.scope.ScopeAdapter;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.test.annotations.TestComponents;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.dockbox.hartshorn.util.introspect.ParameterizableType;
import org.junit.jupiter.api.Test;

import org.dockbox.hartshorn.inject.annotations.Inject;
import test.org.dockbox.hartshorn.inject.scope.ScopedBindingConfiguration.SampleScope;

import static org.assertj.core.api.Assertions.assertThat;

@HartshornIntegrationTest(includeBasePackages = false)
class ScopeBindingTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    void scopeBindingIsNotAccessibleFromApplication() {
        Scope scope = ScopeAdapter.of(new Object(), ParameterizableType.create(Object.class));
        ComponentKey<String> key = ComponentKey.builder(String.class)
            .scope(scope)
            .build();

        this.applicationContext.bind(key).singleton("test");
        String value = this.applicationContext.get(key);
        assertThat(value).isEqualTo("test");

        ComponentKey<String> componentKeyNoScope = ComponentKey.of(String.class);

        String valueNoScope = this.applicationContext.get(componentKeyNoScope);
        assertThat(valueNoScope).isEmpty(); // Default value for primitives
    }

    @Test
    void applicationBindingIsAccessibleFromScope() {
        this.applicationContext.bind(String.class).singleton("test");

        Scope scope = ScopeAdapter.of(new Object(), ParameterizableType.create(Object.class));
        ComponentKey<String> key = ComponentKey.builder(String.class)
            .scope(scope)
            .build();

        String value = this.applicationContext.get(key);
        assertThat(value).isEqualTo("test");
    }

    @Test
    @TestComponents(ScopedBindingConfiguration.class)
    void configurationScopedValuesAreInstalled() {
        String applicationScope = this.applicationContext.get(String.class);
        String scopedValue = this.applicationContext.get(ComponentKey.builder(String.class)
            .scope(new SampleScope())
            .build());
        assertThat(applicationScope).isEmpty();
        assertThat(scopedValue).isEqualTo("test");
    }
}
