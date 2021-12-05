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

package org.dockbox.hartshorn.core.context;

import org.dockbox.hartshorn.core.InjectionPoint;
import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.MetaProvider;
import org.dockbox.hartshorn.core.annotations.context.LogExclude;
import org.dockbox.hartshorn.core.boot.ApplicationLogger;
import org.dockbox.hartshorn.core.boot.ExceptionHandler;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.core.services.ComponentLocator;
import org.dockbox.hartshorn.core.services.ComponentProcessor;
import org.slf4j.Logger;

import java.lang.annotation.Annotation;
import java.util.Set;

@LogExclude
public interface ApplicationContext extends
        ApplicationBinder,
        ComponentProvider,
        ApplicationPropertyHolder,
        ExceptionHandler,
        ApplicationLogger
{

    @Deprecated(since = "4.2.5", forRemoval = true)
    void add(InjectionPoint<?> property);

    <T> T create(Key<T> type);

    @Deprecated(since = "4.2.5", forRemoval = true)
    <T> T inject(Key<T> type, T typeInstance);

    <T> T populate(T type);

    <T> T raw(TypeContext<T> type);

    @Deprecated(since = "4.2.5", forRemoval = true)
    <T> T raw(TypeContext<T> type, boolean populate);

    void add(ComponentProcessor<?> processor);

    Set<Annotation> activators();

    boolean hasActivator(Class<? extends Annotation> activator);

    <A> A activator(Class<A> activator);

    ComponentLocator locator();

    MetaProvider meta();

    ApplicationEnvironment environment();

    <T> T invoke(MethodContext<T, ?> method);

    <T, P> T invoke(MethodContext<T, P> method, P instance);

    @Override
    default Logger log() {
        return this.environment().manager().log();
    }

    default <C extends Context> Exceptional<C> first(final Class<C> context) {
        return this.first(this, context);
    }

    void enable(Object instance) throws ApplicationException;
}
