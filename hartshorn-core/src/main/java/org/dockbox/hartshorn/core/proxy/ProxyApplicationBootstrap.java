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

package org.dockbox.hartshorn.core.proxy;

import org.dockbox.hartshorn.core.InjectConfiguration;
import org.dockbox.hartshorn.core.Modifier;
import org.dockbox.hartshorn.core.MultiMap;
import org.dockbox.hartshorn.core.annotations.inject.InjectPhase;
import org.dockbox.hartshorn.core.annotations.proxy.UseProxying;
import org.dockbox.hartshorn.core.boot.HartshornBootstrap;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;

public class ProxyApplicationBootstrap extends HartshornBootstrap {

    @Override
    public void create(final Collection<String> prefixes, final TypeContext<?> activationSource, final List<Annotation> activators, final MultiMap<InjectPhase, InjectConfiguration> configs, final String[] args, final Modifier... modifiers) {
        activators.add(new UseProxying() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return UseProxying.class;
            }
        });
        super.create(prefixes, activationSource, activators, configs, args, modifiers);
    }

    @Override
    public <T> Exceptional<T> proxy(final TypeContext<T> type, final T instance) {
        return Exceptional.of(() -> JavassistProxyUtil.handler(type, instance).proxy(instance));
    }

    @Override
    public <T> Exceptional<TypeContext<T>> real(final T instance) {
        return JavassistProxyUtil.handler(instance).map(ProxyHandler::type);
    }

    @Override
    public <T, P extends T> Exceptional<T> delegator(final TypeContext<T> type, final P instance) {
        return JavassistProxyUtil.delegator(this.context(), type, instance);
    }

    @Override
    public <T, P extends T> Exceptional<T> delegator(final TypeContext<T> type, final ProxyHandler<P> instance) {
        return JavassistProxyUtil.delegator(this.context(), type, instance);
    }

    @Override
    public <T> ProxyHandler<T> handler(final TypeContext<T> type, final T instance) {
        return JavassistProxyUtil.handler(type, instance);
    }
}
