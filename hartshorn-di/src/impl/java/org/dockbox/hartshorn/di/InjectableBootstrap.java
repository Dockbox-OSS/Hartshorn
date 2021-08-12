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
import org.dockbox.hartshorn.di.context.HartshornApplicationContext;
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
public abstract class InjectableBootstrap extends ApplicationContextAware implements ApplicationBootstrap {

    private static InjectableBootstrap instance;

    public static InjectableBootstrap instance() {
        return (InjectableBootstrap) ApplicationContextAware.instance();
    }

    @Override
    public void create(final Collection<String> prefixes, final Class<?> activationSource, final List<Annotation> activators, final Multimap<InjectPhase, InjectConfiguration> configs, final Modifier... modifiers) {
        super.create(new HartshornApplicationContext(activationSource, modifiers));
        Reflections.log = null; // Don't output Reflections

        final ReflectionContext context = new ReflectionContext(prefixes);
        Reflect.context(context);

        for (final Annotation activator : activators) {
            ((ManagedHartshornContext) this.context()).addActivator(activator);
        }
        instance(this);
        for (final String prefix : prefixes) {
            this.lookupProcessors(prefix);
            this.lookupModifiers(prefix);
        }

        for (final InjectConfiguration config : configs.get(InjectPhase.EARLY)) super.context().bind(config);
        for (final String prefix : prefixes) super.context().bind(prefix);
        for (final InjectConfiguration config : configs.get(InjectPhase.LATE)) super.context().bind(config);
    }

    private void lookupProcessors(final String prefix) {
        final Collection<Class<? extends ServiceProcessor>> processors = Reflect.children(ServiceProcessor.class);
        for (final Class<? extends ServiceProcessor> processor : processors) {
            if (Reflect.isAbstract(processor)) continue;

            final ServiceProcessor raw = super.context().raw(processor, false);
            if (this.context().hasActivator(raw.activator()))
                super.context().add(raw);
        }
    }

    private void lookupModifiers(final String prefix) {
        final Collection<Class<? extends InjectionModifier>> modifiers = Reflect.children(InjectionModifier.class);
        for (final Class<? extends InjectionModifier> modifier : modifiers) {
            if (Reflect.isAbstract(modifier)) continue;

            final InjectionModifier raw = super.context().raw(modifier, false);
            if (this.context().hasActivator(raw.activator()))
                super.context().add(raw);
        }
    }
}
