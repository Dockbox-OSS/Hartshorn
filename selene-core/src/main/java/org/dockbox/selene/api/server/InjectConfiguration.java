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

package org.dockbox.selene.api.server;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import org.dockbox.selene.api.util.SeleneUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class InjectConfiguration extends AbstractModule {

    private final Field factoryBindings;
    private final Field collectorBindings;
    private final Set<Class<?>> requiredTypes;

    protected InjectConfiguration() {
        Field localFactoryBindings;
        try {
            localFactoryBindings = FactoryModuleBuilder.class.getDeclaredField("bindings");
        } catch (ReflectiveOperationException e) {
            localFactoryBindings = null;
            Selene.handle("Unable to obtain factory bindings, verification may fail!", e);
        }
        this.factoryBindings = localFactoryBindings;

        Field localCollectorBindings;
        try {
            Class<?> bindingCollector = Class.forName("com.google.inject.assistedinject.BindingCollector");
            localCollectorBindings = bindingCollector.getDeclaredField("bindings");
        } catch (ReflectiveOperationException e) {
            localCollectorBindings = null;
            Selene.handle("Unable to obtain collector bindings, verification may fail!", e);
        }
        this.collectorBindings = localCollectorBindings;

        this.requiredTypes = SeleneUtils.emptySet();
        try {
            for (Method declaredMethod : SeleneFactory.class.getDeclaredMethods()) {
                this.requiredTypes.add(declaredMethod.getReturnType());
            }
        } catch (SecurityException e) {
            Selene.handle("Unable to obtain required factory types, verification may fail!", e);
        }
    }

    @SuppressWarnings("unchecked")
    protected Module verify(FactoryModuleBuilder factory) {
        try {
            Object collector = this.factoryBindings.get(factory);
            Map<Key<?>, TypeLiteral<?>> bindings = (Map<Key<?>, TypeLiteral<?>>) this.collectorBindings.get(collector);
            Set<Class<?>> rawBindingTypes = bindings.keySet().stream().map(key -> key.getTypeLiteral().getRawType()).collect(Collectors.toSet());

            // Typically a containsAll would do, in this case however we need to log the exact type that does not have a binding
            for (Class<?> requiredType : this.requiredTypes) {
                if (!rawBindingTypes.contains(requiredType)) throw new IllegalStateException("Missing binding for factory type " + requiredType.getCanonicalName());
            }
        } catch (ReflectiveOperationException e) {
            Selene.handle("Unable to verify factory bindings", e);
        }
        return factory.build(SeleneFactory.class);
    }

}
