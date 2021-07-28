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

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.annotations.inject.Named;
import org.dockbox.hartshorn.di.binding.Bindings;
import org.dockbox.hartshorn.di.inject.wired.BoundContext;
import org.dockbox.hartshorn.di.inject.wired.ConstructorBoundContext;
import org.dockbox.hartshorn.di.properties.BindingMetaProperty;
import org.dockbox.hartshorn.di.properties.InjectableType;
import org.dockbox.hartshorn.di.properties.InjectorProperty;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.dockbox.hartshorn.util.Reflect;

import javax.annotation.Nullable;

public class SimpleTypeFactory implements TypeFactory {

    private InjectorProperty<?>[] properties;

    public SimpleTypeFactory() {
        this.properties = new InjectorProperty[0];
    }

    @Override
    public <T> T create(Class<T> type, Object... arguments) {
        @Nullable final InjectorProperty<Named> property = Bindings.property(BindingMetaProperty.KEY, Named.class, this.properties);
        Exceptional<BoundContext<T, T>> binding = ApplicationContextAware.instance().context().firstWire(type, property);
        if (binding.absent()) {
            if (Reflect.isAbstract(type)) throw new IllegalStateException("Could not autowire " + type.getCanonicalName() + " as there is no active binding for it");
            else {
                final BoundContext<T,T> context = new ConstructorBoundContext<>(type, type, "");
                ApplicationContextAware.instance().context().add(context);
                binding = Exceptional.of(context);
            }
        }
        Exceptional<BoundContext<T, T>> finalBinding = binding;

        return Exceptional.of(() -> {
            final T instance = finalBinding.get().create(arguments);
            if (instance instanceof InjectableType && ((InjectableType) instance).canEnable()) {
                ((InjectableType) instance).enable(this.properties);
            }
            return instance;
        }).orNull();
    }

    @Override
    public TypeFactory with(InjectorProperty<?>... properties) {
        SimpleTypeFactory clone = new SimpleTypeFactory();
        clone.properties = HartshornUtils.merge(this.properties, properties);
        return clone;
    }
}
