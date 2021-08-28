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
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.di.inject.InjectionModifier;
import org.dockbox.hartshorn.di.services.ServiceProcessor;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;

@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class InjectableBootstrap extends ApplicationContextAware implements ApplicationBootstrap {

    public static InjectableBootstrap instance() {
        return (InjectableBootstrap) ApplicationContextAware.instance();
    }

    @Override
    public void create(final Collection<String> prefixes, final Class<?> activationSource, final List<Annotation> activators, final Multimap<InjectPhase, InjectConfiguration> configs, final Modifier... modifiers) {
        super.create(new HartshornApplicationContext(this, activationSource, prefixes, modifiers));
        Reflections.log = null; // Don't output Reflections

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

    // TODO: Clean up lookupProcessors and lookupModifiers
    private void lookupProcessors(final String prefix) {
        final Collection<TypeContext<? extends ServiceProcessor>> processors = this.context().environment().children(ServiceProcessor.class);
        for (final TypeContext<? extends ServiceProcessor> processor : processors) {
            if (processor.isAbstract()) continue;

            final ServiceProcessor raw = super.context().raw(processor, false);
            if (this.context().hasActivator(raw.activator()))
                super.context().add(raw);
        }
    }

    private void lookupModifiers(final String prefix) {
        final Collection<TypeContext<? extends InjectionModifier>> modifiers = this.context().environment().children(InjectionModifier.class);
        for (final TypeContext<? extends InjectionModifier> modifier : modifiers) {
            if (modifier.isAbstract()) continue;

            final InjectionModifier raw = super.context().raw(modifier, false);
            if (this.context().hasActivator(raw.activator()))
                super.context().add(raw);
        }
    }
}
