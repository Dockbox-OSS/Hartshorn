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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@TestComponents(ProviderComponent.class)
@HartshornIntegrationTest(includeBasePackages = false)
public class ProvidedMethodTests {

    @Inject
    private ProviderComponent providerComponent;
    @Inject
    private Binder binder;

    @Test
    void testProviderWithoutQualifiers() {
        this.binder.bind(String.class).singleton("Hello World");

        Assertions.assertNotNull(this.providerComponent);
        Assertions.assertInstanceOf(Proxy.class, this.providerComponent);

        String message = this.providerComponent.get();
        Assertions.assertNotNull(message);
        Assertions.assertEquals("Hello World", message);
    }

    @Test
    void testProviderWithNamedQualifier() {
        this.binder.bind(String.class).singleton("Hello World");
        this.binder.bind(ComponentKey.of(String.class, "test")).singleton("Hello Test World");

        Assertions.assertNotNull(this.providerComponent);
        Assertions.assertInstanceOf(Proxy.class, this.providerComponent);

        String message = this.providerComponent.getNamed();
        Assertions.assertNotNull(message);
        Assertions.assertEquals("Hello Test World", message);
    }

    @Test
    void testProviderWithCustomQualifiers() {
        this.binder.bind(String.class).singleton("Hello Red World");
        this.binder.bind(ComponentKey.builder(String.class)
            .qualifier(QualifierKey.of(TypeUtils.annotation(Color.class, Colors.RED)))
            .build()
        ).singleton("Hello Red World");
        this.binder.bind(ComponentKey.builder(String.class)
            .qualifier(QualifierKey.of(TypeUtils.annotation(Color.class, Colors.BLUE)))
            .build()
        ).singleton("Hello Blue World");

        Assertions.assertNotNull(this.providerComponent);
        Assertions.assertInstanceOf(Proxy.class, this.providerComponent);

        String redMessage = this.providerComponent.getRed();
        Assertions.assertNotNull(redMessage);
        Assertions.assertEquals("Hello Red World", redMessage);

        String blueMessage = this.providerComponent.getBlue();
        Assertions.assertNotNull(blueMessage);
        Assertions.assertEquals("Hello Blue World", blueMessage);
    }
}
