/*
 * Copyright 2019-2023 the original author or authors.
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

package test.org.dockbox.hartshorn.boot;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.ComponentLocator;
import org.dockbox.hartshorn.inject.processing.UseServiceProvision;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import jakarta.inject.Inject;

@HartshornTest(includeBasePackages = false)
@UseServiceProvision
@TestInstance(Lifecycle.PER_CLASS)
public class ComponentProvisionTests {

    @Inject
    private ApplicationContext applicationContext;

    public Stream<Arguments> components() {
        return this.applicationContext()
                .get(ComponentLocator.class)
                .containers().stream()
                .map(ComponentContainer::type)
                // org.dockbox.hartshorn.core.types is test-specific and includes a few test-specific types
                // that are not part of the core types, and thus should not be tested here.
                .filter(type -> !type.isDeclaredIn("org.dockbox.hartshorn.core.types"))
                .filter(type -> type.constructors().bound().size() != type.constructors().count())
                .map(TypeView::type)
                .map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("components")
    public void testComponentProvision(final Class<?> component) {
        Assertions.assertDoesNotThrow(() -> {
            final Object instance = this.applicationContext().get(component);
            Assertions.assertNotNull(instance);
        });
    }

    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }
}
