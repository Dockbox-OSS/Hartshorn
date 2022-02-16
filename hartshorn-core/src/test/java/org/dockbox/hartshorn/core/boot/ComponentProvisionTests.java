/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.core.boot;

import org.dockbox.hartshorn.core.annotations.activate.UseServiceProvision;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.services.ComponentContainer;
import org.dockbox.hartshorn.testsuite.HartshornExtension;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import javax.inject.Inject;

import lombok.Getter;

@HartshornTest
@UseServiceProvision
public class ComponentProvisionTests {

    @Inject
    @Getter
    private ApplicationContext applicationContext;

    public static Stream<Arguments> components() {
        return HartshornExtension.createContext(new HartshornApplicationFactory().loadDefaults(), ComponentProvisionTests.class)
                .rethrowUnchecked().get()
                .locator()
                .containers().stream()
                .map(ComponentContainer::type)
                .filter(type -> !type.isDeclaredIn("org.dockbox.hartshorn.core.types"))
                .filter(type -> type.boundConstructors().size() != type.constructors().size())
                .map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("components")
    public void testComponentProvision(final TypeContext<?> component) {
        Assertions.assertDoesNotThrow(() -> {
            final Object instance = this.applicationContext().get(component);
            Assertions.assertNotNull(instance);
        });
    }
}
