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

import org.dockbox.hartshorn.api.HartshornApplication;
import org.dockbox.hartshorn.api.SimpleMetaProvider;
import org.dockbox.hartshorn.di.ApplicationContextAware;
import org.dockbox.hartshorn.di.DefaultModifiers;
import org.dockbox.hartshorn.di.MetaProviderModifier;
import org.dockbox.hartshorn.di.adapter.InjectSources;
import org.dockbox.hartshorn.di.adapter.ServiceSources;
import org.dockbox.hartshorn.di.annotations.activate.Activator;
import org.dockbox.hartshorn.di.annotations.inject.InjectConfig;
import org.dockbox.hartshorn.test.util.JUnitInjector;
import org.dockbox.hartshorn.util.Reflect;

import java.lang.reflect.Field;

import lombok.Getter;

@Activator(injectSource = InjectSources.class, inject = "GUICE", value = JUnit5Bootstrap.class, configs = @InjectConfig(JUnitInjector.class), serviceSource = ServiceSources.class, service = "default")
public final class JUnit5Application {

    @Getter
    private static final JUnitInformation information = new JUnitInformation();

    public static void prepareBootstrap() throws NoSuchFieldException, IllegalAccessException {
        final Field instance = ApplicationContextAware.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);

        final Field context = Reflect.class.getDeclaredField("context");
        context.setAccessible(true);
        context.set(null, null);

        HartshornApplication.create(JUnit5Application.class,
                DefaultModifiers.ACTIVATE_ALL,
                new MetaProviderModifier(SimpleMetaProvider::new)
        ).run();
    }
}
