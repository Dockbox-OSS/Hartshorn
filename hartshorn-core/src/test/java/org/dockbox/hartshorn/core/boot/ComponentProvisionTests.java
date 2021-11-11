/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.core.boot;

import org.dockbox.hartshorn.core.annotations.activate.UseServiceProvision;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.services.ComponentContainer;
import org.dockbox.hartshorn.testsuite.ApplicationAwareTest;
import org.dockbox.hartshorn.testsuite.HartshornRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

@UseServiceProvision
public class ComponentProvisionTests extends ApplicationAwareTest {

    public static Stream<Arguments> components() {
        return HartshornRunner.createContext(ComponentProvisionTests.class)
                .rethrow().get()
                .locator()
                .containers().stream()
                .map(ComponentContainer::type)
                .map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("components")
    public void testComponentProvision(final TypeContext<?> component) {
        Assertions.assertDoesNotThrow(() -> {
            final Object instance = this.context().get(component);
            Assertions.assertNotNull(instance);
        });
    }
}
