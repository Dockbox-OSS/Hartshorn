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

package test.org.dockbox.hartshorn.inject.provided;

import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.inject.binding.Binder;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.proxy.Proxy;
import org.dockbox.hartshorn.test.annotations.TestComponents;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@HartshornIntegrationTest(includeBasePackages = false)
public class ProvidedMethodTests {

    @Test
    @TestComponents(components = ProviderService.class)
    void testProviderService(@Inject ProviderService service, @Inject Binder binder) {
        binder.bind(String.class).singleton("Hello World");

        Assertions.assertNotNull(service);
        Assertions.assertInstanceOf(Proxy.class, service);

        String message = service.get();
        Assertions.assertNotNull(message);
        Assertions.assertEquals("Hello World", message);
    }
}
