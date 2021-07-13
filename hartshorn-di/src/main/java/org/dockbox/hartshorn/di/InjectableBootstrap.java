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

package org.dockbox.hartshorn.di;

import com.google.common.collect.Multimap;

import org.dockbox.hartshorn.di.annotations.inject.InjectPhase;
import org.dockbox.hartshorn.di.context.ManagedHartshornContext;
import org.dockbox.hartshorn.di.context.ReflectionContext;
import org.dockbox.hartshorn.di.inject.InjectionModifier;
import org.dockbox.hartshorn.di.services.ServiceProcessor;
import org.dockbox.hartshorn.util.Reflect;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;

@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class InjectableBootstrap extends ApplicationContextAware {

    private static InjectableBootstrap instance;

    public abstract void init();

    public void create(Collection<String> prefixes, Class<?> activationSource, List<Annotation> activators, Multimap<InjectPhase, InjectConfiguration> configs, Modifier... modifiers) {
        super.create(activationSource, modifiers);
        Reflections.log = null; // Don't output Reflections

        final ReflectionContext context = new ReflectionContext(prefixes);
        Reflect.context(context);

        for (Annotation activator : activators) {
            ((ManagedHartshornContext) this.context()).addActivator(activator);
        }
        instance(this);
        for (String prefix : prefixes) {
            this.lookupProcessors(prefix);
            this.lookupModifiers(prefix);
        }

        for (InjectConfiguration config : configs.get(InjectPhase.EARLY)) super.context().bind(config);
        for (String prefix : prefixes) super.context().bind(prefix);
        for (InjectConfiguration config : configs.get(InjectPhase.LATE)) super.context().bind(config);
    }

    private void lookupProcessors(String prefix) {
        final Collection<Class<? extends ServiceProcessor>> processors = Reflect.children(ServiceProcessor.class);
        for (Class<? extends ServiceProcessor> processor : processors) {
            if (Reflect.isAbstract(processor)) continue;

            final ServiceProcessor raw = super.context().raw(processor, false);
            if (this.context().hasActivator(raw.activator()))
                super.context().add(raw);
        }
    }

    private void lookupModifiers(String prefix) {
        final Collection<Class<? extends InjectionModifier>> modifiers = Reflect.children(InjectionModifier.class);
        for (Class<? extends InjectionModifier> modifier : modifiers) {
            if (Reflect.isAbstract(modifier)) continue;

            final InjectionModifier raw = super.context().raw(modifier, false);
            if (this.context().hasActivator(raw.activator()))
                super.context().add(raw);
        }
    }

    public static InjectableBootstrap instance() {
        return (InjectableBootstrap) ApplicationContextAware.instance();
    }
}
