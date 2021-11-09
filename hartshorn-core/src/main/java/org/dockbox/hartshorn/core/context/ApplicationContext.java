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
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.exceptions.TypeProvisionException;
import org.dockbox.hartshorn.core.inject.InjectionModifier;
import org.dockbox.hartshorn.core.services.ComponentLocator;
import org.dockbox.hartshorn.core.services.ComponentProcessor;
import org.slf4j.Logger;

import java.lang.annotation.Annotation;
import java.util.List;

@LogExclude
public interface ApplicationContext extends ApplicationBinder, HartshornContext, ApplicationPropertyHolder {

    void add(InjectionPoint<?> property);

    <T> T create(Key<T> type, T typeInstance);

    <T> T inject(Key<T> type, T typeInstance);

    <T> void enableFields(T typeInstance);

    <T> T raw(TypeContext<T> type) throws TypeProvisionException;

    <T> T raw(TypeContext<T> type, boolean populate) throws TypeProvisionException;

    void add(ComponentProcessor<?> processor);

    void add(InjectionModifier<?> modifier);

    List<Annotation> activators();

    boolean hasActivator(Class<? extends Annotation> activator);

    <A> A activator(Class<A> activator);

    ComponentLocator locator();

    MetaProvider meta();

    ApplicationEnvironment environment();

    void reset();

    default Logger log() {
        return this.environment().application().log();
    }

    default <C extends Context> Exceptional<C> first(final Class<C> context) {
        return this.first(this, context);
    }

    @Override
    <C extends Context> Exceptional<C> first(ApplicationContext applicationContext, Class<C> context);
}
