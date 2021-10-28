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

package org.dockbox.hartshorn.core;

import org.dockbox.hartshorn.core.annotations.inject.InjectPhase;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.HartshornApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.inject.InjectionModifier;
import org.dockbox.hartshorn.core.services.ComponentProcessor;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

public abstract class InjectableBootstrap extends ApplicationContextAware {

    @Override
    public void create(final Collection<String> prefixes, final TypeContext<?> activationSource, final List<Annotation> activators, final MultiMap<InjectPhase, InjectConfiguration> configs, final String[] args, final Modifier... modifiers) {
        super.create(new HartshornApplicationContext(this, activationSource, prefixes, args, modifiers));
        Reflections.log = null; // Don't output Reflections

        for (final Annotation activator : activators) {
            ((HartshornApplicationContext) this.context()).addActivator(activator);
        }

        for (final String prefix : prefixes) {
            this.lookup(prefix, ComponentProcessor.class, ApplicationContext::add);
            this.lookup(prefix, InjectionModifier.class, ApplicationContext::add);
        }

        for (final InjectConfiguration config : configs.get(InjectPhase.EARLY)) super.context().bind(config);
        for (final String prefix : prefixes) super.context().bind(prefix);
        for (final InjectConfiguration config : configs.get(InjectPhase.LATE)) super.context().bind(config);
    }

    private <T extends ActivatorFiltered<?>> void lookup(final String prefix, final Class<T> type, final BiConsumer<ApplicationContext, T> consumer) {
        final Collection<TypeContext<? extends T>> children = this.context().environment().children(type);
        for (final TypeContext<? extends T> child : children) {
            if (child.isAbstract()) continue;

            final T raw = this.context().raw(child, false);
            if (this.context().hasActivator(raw.activator()))
                consumer.accept(this.context(), raw);
        }
    }
}
