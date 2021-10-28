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

import org.dockbox.hartshorn.core.ApplicationBootstrap;
import org.dockbox.hartshorn.core.InjectConfiguration;
import org.dockbox.hartshorn.core.annotations.activate.Activator;
import org.dockbox.hartshorn.core.annotations.inject.InjectConfig;
import org.dockbox.hartshorn.core.annotations.inject.InjectPhase;
import org.dockbox.hartshorn.core.boot.Hartshorn;

import java.lang.annotation.Annotation;

public class JUnitActivator implements Activator {
    @Override
    public Class<? extends ApplicationBootstrap> value() {
        return JUnitBootstrap.class;
    }

    @Override
    public String prefix() {
        return Hartshorn.PACKAGE_PREFIX;
    }

    @Override
    public InjectConfig[] configs() {
        return new InjectConfig[] { new InjectConfig() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return InjectConfig.class;
            }

            @Override
            public Class<? extends InjectConfiguration> value() {
                return JUnitInjector.class;
            }

            @Override
            public InjectPhase phase() {
                return InjectPhase.EARLY;
            }
        } };
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return Activator.class;
    }
}
