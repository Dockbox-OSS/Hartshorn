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

import org.dockbox.hartshorn.boot.Hartshorn;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.proxy.ProxyApplicationBootstrap;

public class JUnit5Bootstrap extends ProxyApplicationBootstrap {

    @Override
    public boolean isCI() {
        return true;
    }

    @Override
    protected void handleMissingBinding(final TypeContext<?> type) {
        Hartshorn.log().warn("Ignoring missing binding for " + type.name());
    }
}
