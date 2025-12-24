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

package test.org.dockbox.hartshorn.inject.provided;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.QualifierKey;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.inject.binding.Binder;
import org.dockbox.hartshorn.proxy.Proxy;
import org.dockbox.hartshorn.test.annotations.TestComponents;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.dockbox.hartshorn.util.types.TypeUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@TestComponents(ProviderComponent.class)
@HartshornIntegrationTest(includeBasePackages = false)
class ProvidedMethodTests {

    @Inject
    private ProviderComponent providerComponent;
    @Inject
    private Binder binder;

    @Test
    void providerWithoutQualifiers() {
        this.binder.bind(String.class).singleton("Hello World");

        assertThat(this.providerComponent)
                .isInstanceOf(Proxy.class);

        String message = this.providerComponent.get();
        assertThat(message)
                .isNotNull()
                .isEqualTo("Hello World");
    }

    @Test
    void providerWithNamedQualifier() {
        this.binder.bind(String.class).singleton("Hello World");
        this.binder.bind(ComponentKey.of(String.class, "test")).singleton("Hello Test World");

        assertThat(this.providerComponent)
                .isInstanceOf(Proxy.class);

        String message = this.providerComponent.getNamed();
        assertThat(message)
                .isNotNull()
                .isEqualTo("Hello Test World");
    }

    @Test
    void providerWithCustomQualifiers() {
        this.binder.bind(String.class).singleton("Hello Red World");
        this.binder.bind(ComponentKey.builder(String.class)
            .qualifier(QualifierKey.of(TypeUtils.annotation(Color.class, Colors.RED)))
            .build()
        ).singleton("Hello Red World");
        this.binder.bind(ComponentKey.builder(String.class)
            .qualifier(QualifierKey.of(TypeUtils.annotation(Color.class, Colors.BLUE)))
            .build()
        ).singleton("Hello Blue World");

        assertThat(this.providerComponent)
                .isInstanceOf(Proxy.class);

        String redMessage = this.providerComponent.getRed();
        assertThat(redMessage)
                .isNotNull()
                .isEqualTo("Hello Red World");

        String blueMessage = this.providerComponent.getBlue();
        assertThat(blueMessage)
                .isNotNull()
                .isEqualTo("Hello Blue World");
    }
}
