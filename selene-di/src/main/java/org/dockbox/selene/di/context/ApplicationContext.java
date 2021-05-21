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

package org.dockbox.selene.di.context;

import org.dockbox.selene.di.InjectionPoint;
import org.dockbox.selene.di.ProvisionFailure;
import org.dockbox.selene.di.inject.Injector;
import org.dockbox.selene.di.properties.InjectorProperty;
import org.dockbox.selene.di.services.ServiceModifier;
import org.dockbox.selene.di.services.ServiceProcessor;

import java.lang.annotation.Annotation;
import java.util.List;

public interface ApplicationContext extends ApplicationBinder, SeleneContext {

    void add(InjectionPoint<?> property);

    <T> T create(Class<T> type, T typeInstance, InjectorProperty<?>... properties);

    <T> T inject(Class<T> type, T typeInstance, InjectorProperty<?>... properties);

    <T> void enable(T typeInstance);

    <T> T raw(Class<T> type) throws ProvisionFailure;
    <T> T raw(Class<T> type, boolean populate) throws ProvisionFailure;

    Injector injector();

    void add(ServiceProcessor<?> processor);
    void add(ServiceModifier<?> modifier);

    Class<?> getActivationSource();
    List<Annotation> activators();
    boolean hasActivator(Class<? extends Annotation> activator);

}
