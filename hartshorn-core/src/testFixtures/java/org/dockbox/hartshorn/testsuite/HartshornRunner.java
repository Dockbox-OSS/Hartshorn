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

package org.dockbox.hartshorn.testsuite;

import org.dockbox.hartshorn.core.annotations.activate.Activator;
import org.dockbox.hartshorn.core.annotations.service.ServiceActivator;
import org.dockbox.hartshorn.core.boot.HartshornApplicationFactory;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.Mockito;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;

@Activator
public class HartshornRunner implements BeforeEachCallback, AfterEachCallback{

    @Getter private static final JUnitInformation information = new JUnitInformation();

    @Getter private ApplicationContext activeContext;

    @Override
    public void beforeEach(final ExtensionContext context) {
        final Optional<Class<?>> testClass = context.getTestClass();
        if (testClass.isEmpty()) throw new IllegalStateException("Test class was not provided to runner");

        this.activeContext = createContext(testClass.get()).orNull();
    }

    public static Exceptional<ApplicationContext> createContext(final Class<?> activator) {
        final Set<Annotation> serviceActivators = TypeContext.of(activator).annotations().stream()
                .filter(annotation -> TypeContext.of(annotation.annotationType()).annotation(ServiceActivator.class).present())
                .collect(Collectors.toSet());

        return Exceptional.of(() -> new HartshornApplicationFactory()
                .loadDefaults()
                .serviceActivators(serviceActivators)
                .configuration(new JUnitInjector())
                .activator(TypeContext.of(HartshornRunner.class))
                .create());
    }

    @Override
    public void afterEach(final ExtensionContext context) {
        // To ensure static mocking does not affect other tests
        Mockito.clearAllCaches();
    }
}
