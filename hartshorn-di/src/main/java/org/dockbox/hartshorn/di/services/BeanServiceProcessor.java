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

package org.dockbox.hartshorn.di.services;

import com.google.inject.Key;

import org.dockbox.hartshorn.di.annotations.inject.Bean;
import org.dockbox.hartshorn.di.annotations.activate.UseBeanProvision;
import org.dockbox.hartshorn.di.annotations.inject.Wired;
import org.dockbox.hartshorn.di.binding.Bindings;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.inject.BeanContext;
import org.dockbox.hartshorn.di.inject.wired.BeanWireContext;
import org.dockbox.hartshorn.di.inject.wired.WireContext;
import org.dockbox.hartshorn.util.Reflect;

import java.lang.reflect.Method;
import java.util.Collection;

import javax.inject.Singleton;

public final class BeanServiceProcessor implements ServiceProcessor<UseBeanProvision> {

    @Override
    public boolean preconditions(Class<?> type) {
        return !Reflect.methods(type, Bean.class).isEmpty();
    }

    @Override
    public <T> void process(ApplicationContext context, Class<T> type) {
        Collection<Method> beans = Reflect.methods(type, Bean.class);
        for (Method bean : beans) {
            boolean singleton = Reflect.annotation(bean, Singleton.class).present();
            Bean annotation = Reflect.annotation(bean, Bean.class).get();

            if (Reflect.annotation(bean, Wired.class).present()) {
                if (singleton) throw new IllegalArgumentException("Cannot provide manually wired singleton bean " + bean.getReturnType() + " at " + bean.getName());
                else {
                    WireContext<?, ?> wireContext = new BeanWireContext<>(bean.getReturnType(), bean, annotation.value());
                    context.add(wireContext);
                }
            }
            else {
                Key<?> key = "".equals(annotation.value())
                        ? Key.get(bean.getReturnType())
                        : Key.get(bean.getReturnType(), Bindings.named(annotation.value()));

                BeanContext<?, ?> beanContext = new BeanContext<>(key, singleton, () -> context.invoke(bean));
                context.add(beanContext);
            }
        }
    }

    @Override
    public Class<UseBeanProvision> activator() {
        return UseBeanProvision.class;
    }
}
