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

import org.dockbox.selene.api.BootstrapPhase;
import org.dockbox.selene.api.Phase;
import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.SeleneInformation;
import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.domain.OwnerLookup;
import org.dockbox.selene.api.domain.TypedOwner;
import org.dockbox.selene.api.i18n.annotations.Resource;
import org.dockbox.selene.api.i18n.annotations.Resources;
import org.dockbox.selene.api.i18n.common.ResourceEntry;
import org.dockbox.selene.api.i18n.exceptions.ProxyFactoryBindingException;
import org.dockbox.selene.api.i18n.exceptions.ProxyMethodBindingException;
import org.dockbox.selene.di.preload.Preloadable;
import org.dockbox.selene.di.Provider;
import org.dockbox.selene.proxy.ProxyProperty;
import org.dockbox.selene.proxy.handle.ProxyHandler;
import org.dockbox.selene.util.Reflect;

import java.lang.reflect.Method;

@Phase(BootstrapPhase.CONSTRUCT)
public class I18NPreload implements Preloadable {

    @SuppressWarnings("unchecked")
    @Override
    public void preload() {
        for (Class<?> annotatedType : Reflect.annotatedTypes(SeleneInformation.PACKAGE_PREFIX, Resources.class)) {
            if (!annotatedType.isInterface())
                throw new ProxyFactoryBindingException(annotatedType);

            Selene.getServer().getInjector().bind((Class<Object>) annotatedType, this.createResourceProxy(annotatedType));
        }
        Reflect.registerModulePostInit(Provider.provide(ResourceService.class)::init);
    }

    @SuppressWarnings("unchecked")
    private <T, C extends T> C createResourceProxy(Class<T> type) {
        ProxyHandler<Object> handler = new ProxyHandler<>(null, (Class<Object>) type);

        String prefix = "";
        if (type.isAnnotationPresent(Resources.class)) {
            Class<?> responsibleClass = type.getAnnotation(Resources.class).value();
            TypedOwner lookup = Provider.provide(OwnerLookup.class).lookup(responsibleClass);
            if (lookup != null) prefix = lookup.id() + '.';
        }

        for (Method annotatedMethod : Reflect.annotatedMethods(type, Resource.class)) {
            if (!Reflect.assignableFrom(ResourceEntry.class, annotatedMethod.getReturnType()))
                throw new ProxyMethodBindingException(annotatedMethod);

            String key = this.extractKey(annotatedMethod, prefix);
            Resource annotation = annotatedMethod.getAnnotation(Resource.class);

            ProxyProperty<?, ResourceEntry> property = ProxyProperty.of(type, annotatedMethod, (instance, args) -> {
                // Prevents NPE when formatting cached resources without arguments
                Object[] objects = null == args ? new Object[0] : args;
                return Provider.provide(ResourceService.class).getOrCreate(key, annotation.value()).format(objects);
            });

            handler.delegate((ProxyProperty<Object, ?>) property);
        }
        return Exceptional.of(handler::proxy).map(p -> (C) p).orNull();
    }

    private String extractKey(Method method, String prefix) {
        if (method.isAnnotationPresent(Resource.class)) {
            String key = method.getAnnotation(Resource.class).key();
            if (!"".equals(key)) return key;
        }
        String keyJoined = method.getName();
        if (keyJoined.startsWith("get")) keyJoined = keyJoined.substring(3);
        String[] r = keyJoined.split("(?=\\p{Lu})");
        return prefix + String.join(".", r).toLowerCase();
    }
}
