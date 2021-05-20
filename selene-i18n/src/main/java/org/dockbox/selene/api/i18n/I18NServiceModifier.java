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

package org.dockbox.selene.api.i18n;

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.domain.OwnerLookup;
import org.dockbox.selene.api.domain.TypedOwner;
import org.dockbox.selene.api.i18n.annotations.Resource;
import org.dockbox.selene.api.i18n.common.ResourceEntry;
import org.dockbox.selene.api.i18n.exceptions.ProxyMethodBindingException;
import org.dockbox.selene.di.annotations.Service;
import org.dockbox.selene.di.context.ApplicationContext;
import org.dockbox.selene.di.properties.InjectorProperty;
import org.dockbox.selene.proxy.ServiceAnnotatedMethodModifier;
import org.dockbox.selene.proxy.handle.ProxyFunction;
import org.dockbox.selene.util.Reflect;
import org.dockbox.selene.util.SeleneUtils;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public class I18NServiceModifier implements ServiceAnnotatedMethodModifier<Resource> {

    @SuppressWarnings("unchecked")
    @Override
    public <T, R> ProxyFunction<T, R> process(ApplicationContext context, Class<T> type, @Nullable T instance, Method method, InjectorProperty<?>... properties) {
        if (!Reflect.assignableFrom(ResourceEntry.class, method.getReturnType()))
            throw new ProxyMethodBindingException(method);

        String prefix = "";
        if (type.isAnnotationPresent(Service.class)) {
            TypedOwner lookup = Selene.context().get(OwnerLookup.class).lookup(type);
            if (lookup != null) prefix = lookup.id() + '.';
        }

        String key = this.extractKey(method, prefix);
        Resource annotation = method.getAnnotation(Resource.class);

        return (self, args, holder) -> {
            // Prevents NPE when formatting cached resources without arguments
            Object[] objects = null == args ? new Object[0] : args;
            return (R) Selene.context().get(ResourceService.class).getOrCreate(key, annotation.value()).format(objects);
        };
    }

    @Override
    public <T> boolean preconditions(Class<T> type, @Nullable T instance, Method method, InjectorProperty<?>... properties) {
        return Reflect.assignableFrom(ResourceEntry.class, method.getReturnType());
    }

    @Override
    public boolean failOnPrecondition() {
        return true;
    }

    @Override
    public Class<Resource> annotation() {
        return Resource.class;
    }

    private String extractKey(Method method, String prefix) {
        if (method.isAnnotationPresent(Resource.class)) {
            String key = method.getAnnotation(Resource.class).key();
            if (!"".equals(key)) return key;
        }
        String keyJoined = method.getName();
        if (keyJoined.startsWith("get")) keyJoined = keyJoined.substring(3);
        String[] r = SeleneUtils.splitCapitals(keyJoined);
        return prefix + String.join(".", r).toLowerCase();
    }
}
