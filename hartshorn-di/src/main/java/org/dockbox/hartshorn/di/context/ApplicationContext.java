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

package org.dockbox.hartshorn.di.context;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.InjectionPoint;
import org.dockbox.hartshorn.di.Key;
import org.dockbox.hartshorn.di.MetaProvider;
import org.dockbox.hartshorn.di.ProvisionFailure;
import org.dockbox.hartshorn.di.annotations.context.LogExclude;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.di.inject.InjectionModifier;
import org.dockbox.hartshorn.di.properties.Attribute;
import org.dockbox.hartshorn.di.services.ComponentLocator;
import org.dockbox.hartshorn.di.services.ComponentProcessor;
import org.slf4j.Logger;

import java.lang.annotation.Annotation;
import java.util.List;

@LogExclude
public interface ApplicationContext extends ApplicationBinder, HartshornContext, ApplicationPropertyHolder {

    void add(InjectionPoint<?> property);

    <T> T create(Key<T> type, T typeInstance, Attribute<?>... properties);

    <T> T inject(Key<T> type, T typeInstance, Attribute<?>... properties);

    <T> void enable(T typeInstance);

    <T> T raw(TypeContext<T> type) throws ProvisionFailure;

    <T> T raw(TypeContext<T> type, boolean populate) throws ProvisionFailure;

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
