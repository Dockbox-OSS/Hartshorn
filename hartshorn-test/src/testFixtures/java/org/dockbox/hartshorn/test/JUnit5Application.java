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

import org.dockbox.hartshorn.boot.HartshornApplication;
import org.dockbox.hartshorn.boot.MetaProviderImpl;
import org.dockbox.hartshorn.di.ApplicationContextAware;
import org.dockbox.hartshorn.di.DefaultModifiers;
import org.dockbox.hartshorn.di.MetaProviderModifier;
import org.dockbox.hartshorn.di.annotations.activate.Activator;
import org.dockbox.hartshorn.di.annotations.inject.InjectConfig;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.test.util.JUnitInjector;

import java.lang.reflect.Field;

import lombok.Getter;

@Activator(value = JUnit5Bootstrap.class, configs = @InjectConfig(JUnitInjector.class))
public final class JUnit5Application {

    @Getter private static final JUnitInformation information = new JUnitInformation();

    public static ApplicationContext prepareBootstrap() throws NoSuchFieldException, IllegalAccessException {
        final Field instance = ApplicationContextAware.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);

        return HartshornApplication.create(JUnit5Application.class,
                DefaultModifiers.ACTIVATE_ALL,
                new MetaProviderModifier(MetaProviderImpl::new)
        );
    }
}
