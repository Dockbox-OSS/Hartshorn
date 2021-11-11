/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.core.boot;

import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.proxy.ProxyHandler;

public interface ApplicationProxier {

    <T> Exceptional<T> proxy(TypeContext<T> type, T instance);

    <T> Exceptional<TypeContext<T>> real(T instance);

    <T, P extends T> Exceptional<T> delegator(final TypeContext<T> type, P instance);

    <T, P extends T> Exceptional<T> delegator(final TypeContext<T> type, ProxyHandler<P> handler);

    <T> ProxyHandler<T> handler(final TypeContext<T> type, final T instance);

}
