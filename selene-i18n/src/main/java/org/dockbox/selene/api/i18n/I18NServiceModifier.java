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
import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.domain.OwnerLookup;
import org.dockbox.selene.api.domain.TypedOwner;
import org.dockbox.selene.api.i18n.annotations.Resource;
import org.dockbox.selene.api.i18n.common.ResourceEntry;
import org.dockbox.selene.api.i18n.exceptions.ProxyMethodBindingException;
import org.dockbox.selene.di.annotations.Service;
import org.dockbox.selene.di.context.ApplicationContext;
import org.dockbox.selene.di.properties.InjectorProperty;
import org.dockbox.selene.di.services.ServiceModifier;
import org.dockbox.selene.proxy.ProxyProperty;
import org.dockbox.selene.proxy.handle.ProxyHandler;
import org.dockbox.selene.util.Reflect;
import org.dockbox.selene.util.SeleneUtils;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

public class I18NServiceModifier implements ServiceModifier {
    @Override
    public <T> boolean preconditions(Class<T> type, @Nullable T instance, InjectorProperty<?>... properties) {
        final Collection<Method> methods = Reflect.annotatedMethods(type, Resource.class);
        final Collection<Field> fields = Reflect.annotatedFields(type, Resource.class);
        return !(methods.isEmpty() && fields.isEmpty());
    }

    @Override
    public <T> T process(ApplicationContext context, Class<T> type, @Nullable T instance, InjectorProperty<?>... properties) {
        ProxyHandler<T> handler = new ProxyHandler<>(instance, type);

        String prefix = "";
        if (type.isAnnotationPresent(Service.class)) {
            TypedOwner lookup = Selene.context().get(OwnerLookup.class).lookup(type);
            if (lookup != null) prefix = lookup.id() + '.';
        }

        for (Method annotatedMethod : Reflect.annotatedMethods(type, Resource.class)) {
            if (!Reflect.assignableFrom(ResourceEntry.class, annotatedMethod.getReturnType()))
                throw new ProxyMethodBindingException(annotatedMethod);

            String key = this.extractKey(annotatedMethod, prefix);
            Resource annotation = annotatedMethod.getAnnotation(Resource.class);

            ProxyProperty<T, ResourceEntry> property = ProxyProperty.of(type, annotatedMethod, (self, args) -> {
                // Prevents NPE when formatting cached resources without arguments
                Object[] objects = null == args ? new Object[0] : args;
                return Selene.context().get(ResourceService.class).getOrCreate(key, annotation.value()).format(objects);
            });

            handler.delegate(property);
        }
        return Exceptional.of(handler::proxy).orNull();
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
