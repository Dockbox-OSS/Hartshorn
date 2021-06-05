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

package org.dockbox.hartshorn.api;

import org.dockbox.hartshorn.api.annotations.PostBootstrap;
import org.dockbox.hartshorn.api.annotations.UseBootstrap;
import org.dockbox.hartshorn.di.annotations.Service;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.services.ServiceProcessor;
import org.dockbox.hartshorn.util.Reflect;

import java.lang.reflect.Method;
import java.util.Collection;

public class PostBootstrapServiceProcessor implements ServiceProcessor<UseBootstrap> {

    @Override
    public boolean preconditions(Class<?> type) {
        boolean activated = type.getAnnotation(Service.class).activator().equals(UseBootstrap.class);
        boolean hasPosts = !Reflect.annotatedMethods(type, PostBootstrap.class).isEmpty();
        return activated && hasPosts;
    }

    @Override
    public <T> void process(ApplicationContext context, Class<T> type) {
        final Collection<Method> methods = Reflect.annotatedMethods(type, PostBootstrap.class);
        for (Method method : methods) {
            HartshornBootstrap.instance().addPostBootstrapActivation(method, type);
        }
    }

    @Override
    public Class<UseBootstrap> activator() {
        return UseBootstrap.class;
    }
}
