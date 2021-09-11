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

package org.dockbox.hartshorn.boot;

import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.di.services.ComponentContainer;
import org.dockbox.hartshorn.test.ApplicationAwareTest;
import org.dockbox.hartshorn.test.JUnit5Application;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.stream.Stream;

public class ComponentProvisionTests extends ApplicationAwareTest {

    /**
     * Used to indicate which services should not be tested, typically because
     * they are targeted by other tests and/or are intended to fail.
     */
    private static final Collection<Class<?>> excluded = HartshornUtils.asList(
            UnactivatedPostBootstrapService.class,
            EmptyPostBootstrapService.class,
            ValidPostBootstrapService.class
    );

    public static Stream<Arguments> components() throws NoSuchFieldException, IllegalAccessException {
        return JUnit5Application.prepareBootstrap().locator()
                .containers().stream()
                .map(ComponentContainer::type)
                .filter(type -> !excluded.contains(type.type()))
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
