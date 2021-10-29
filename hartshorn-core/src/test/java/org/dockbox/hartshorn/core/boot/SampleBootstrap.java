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

package org.dockbox.hartshorn.core.boot;

import org.dockbox.hartshorn.core.InjectConfiguration;
import org.dockbox.hartshorn.core.Modifier;
import org.dockbox.hartshorn.core.MultiMap;
import org.dockbox.hartshorn.core.annotations.inject.InjectPhase;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;

public class SampleBootstrap extends HartshornBootstrap {

    @Override
    public boolean isCI() {
        return true;
    }

    @Override
    public void create(final Collection<String> prefixes, final TypeContext<?> activationSource, final List<Annotation> activators, final MultiMap<InjectPhase, InjectConfiguration> configs, final String[] args, final Modifier... modifiers) {
        // This is only used for Application testing, do not actually create bootstrap instance
    }

    @Override
    public <T> Exceptional<T> proxy(final TypeContext<T> type, final T instance) {
        return Exceptional.empty();
    }

    @Override
    public <T> Exceptional<TypeContext<T>> real(final T instance) {
        return Exceptional.empty();
    }

    @Override
    public <T, P extends T> Exceptional<T> delegator(final TypeContext<T> type, final P instance) {
        return Exceptional.empty();
    }
}
