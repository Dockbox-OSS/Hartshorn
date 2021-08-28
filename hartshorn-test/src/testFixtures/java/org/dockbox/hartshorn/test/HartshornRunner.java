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

package org.dockbox.hartshorn.test;

import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.Mockito;

import lombok.Getter;

public class HartshornRunner implements BeforeEachCallback, AfterEachCallback{

    @Getter private ApplicationContext activeContext;

    @Override
    public void beforeEach(final ExtensionContext context) throws Exception {
        this.activeContext = JUnit5Application.prepareBootstrap();
    }

    @Override
    public void afterEach(final ExtensionContext context) {
        // To ensure static mocking does not affect other tests
        Mockito.clearAllCaches();
    }
}
