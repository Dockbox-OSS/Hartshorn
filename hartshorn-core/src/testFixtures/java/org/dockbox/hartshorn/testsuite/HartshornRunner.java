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
import org.dockbox.hartshorn.core.boot.HartshornApplication;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.Mockito;

import java.util.Optional;

import lombok.Getter;

public class HartshornRunner implements BeforeEachCallback, AfterEachCallback{

    @Getter private static final JUnitInformation information = new JUnitInformation();
    @Getter private static final Activator activator = new JUnitActivator();

    @Getter private ApplicationContext activeContext;

    @Override
    public void beforeEach(final ExtensionContext context) {
        final Optional<Class<?>> testClass = context.getTestClass();
        if (testClass.isEmpty()) throw new IllegalStateException("Test class was not provided to runner");

        this.activeContext = createContext(testClass.get()).orNull();
    }

    public static Exceptional<ApplicationContext> createContext(final Class<?> activator) {
        return Exceptional.of(() -> HartshornApplication.load(
                new JUnitBootstrap(),
                HartshornRunner.activator,
                new JUnitActivatorContext<>(activator),
                new String[0]
        ).load()).rethrow();
    }

    @Override
    public void afterEach(final ExtensionContext context) {
        // To ensure static mocking does not affect other tests
        Mockito.clearAllCaches();
    }
}
