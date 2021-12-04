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

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.junit.jupiter.api.extension.RegisterExtension;

/**
 * A test class that can be extended by test classes that need to access the {@link ApplicationContext}.
 *
 * @deprecated Use {@link HartshornTest} instead. If the {@link ApplicationContext} is needed, it should be
 * added to the test class as an injected field.
 */
@Deprecated(since = "4.2.5", forRemoval = true)
public abstract class ApplicationAwareTest {

    @RegisterExtension
    final HartshornExtension runner = new HartshornExtension();

    @Deprecated(since = "4.2.5", forRemoval = true)
    protected ApplicationContext context() {
        final ApplicationContext activeContext = null;
        if (activeContext == null) {
            throw new IllegalStateException("Active state was lost, was it modified externally?");
        }
        return activeContext;
    }
}
