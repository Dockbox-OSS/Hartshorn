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

package org.dockbox.selene.di;

import org.dockbox.selene.di.properties.InjectorProperty;
import org.dockbox.selene.di.services.ServiceModifier;
import org.dockbox.selene.di.services.ServiceProcessor;
import org.dockbox.selene.util.Reflect;

import java.util.Collection;

@SuppressWarnings({ "AbstractClassWithoutAbstractMethods", "unchecked", "rawtypes" })
public abstract class InjectableBootstrap extends ApplicationContextAware {

    private static InjectableBootstrap instance;

    protected InjectableBootstrap(String prefix, Class<?> activationSource) {
        super(activationSource);
        this.lookupProcessors(prefix);
        this.lookupModifiers(prefix);
        instance(this);
    }

    private void lookupProcessors(String prefix) {
        final Collection<Class<? extends ServiceProcessor>> processors = Reflect.subTypes(prefix, ServiceProcessor.class);
        for (Class<? extends ServiceProcessor> processor : processors) {
            if (!Reflect.isConcrete(processor)) continue;

            final ServiceProcessor raw = super.getContext().raw(processor, false);
            if (this.getContext().hasActivator(raw.activator()))
                super.getContext().add(raw);
        }
    }

    private void lookupModifiers(String prefix) {
        final Collection<Class<? extends ServiceModifier>> modifiers = Reflect.subTypes(prefix, ServiceModifier.class);
        for (Class<? extends ServiceModifier> modifier : modifiers) {
            if (!Reflect.isConcrete(modifier)) continue;

            final ServiceModifier raw = super.getContext().raw(modifier, false);
            if (this.getContext().hasActivator(raw.activator()))
                super.getContext().add(raw);
        }
    }

    public static InjectableBootstrap instance() {
        return (InjectableBootstrap) ApplicationContextAware.instance();
    }

    /**
     * Gets an instance of a provided {@link Class} type.
     *
     * @param <T>
     *         The type parameter for the instance to return
     * @param type
     *         The type of the instance
     * @param additionalProperties
     *         The properties to be passed into the type either during or after
     *         construction
     *
     * @return The instance, if present. Otherwise returns null
     */
    public <T> T instance(Class<T> type, InjectorProperty<?>... additionalProperties) {
        return null;
    }
}
