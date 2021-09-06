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
import org.dockbox.hartshorn.di.binding.Bindings;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.di.inject.wired.BoundContext;
import org.dockbox.hartshorn.di.inject.wired.ConstructorBoundContext;
import org.dockbox.hartshorn.di.properties.Attribute;
import org.dockbox.hartshorn.di.properties.BindingMetaAttribute;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;

public class TypeFactoryImpl implements TypeFactory {

    @Inject
    @Getter
    private ApplicationContext applicationContext;
    private Attribute<?>[] properties;

    public TypeFactoryImpl() {
        this.properties = new Attribute[0];
    }

    @Override
    public <T> T create(final TypeContext<T> type, final Object... arguments) {
        @Nullable Named named = null;
        for (final Attribute<?> property : this.properties) {
            if (property instanceof BindingMetaAttribute bindingMeta) named = bindingMeta.value();
        }

        Exceptional<BoundContext<T, T>> binding = this.applicationContext.firstWire(Key.of(type, named));
        if (binding.absent()) {
            if (type.isAbstract()) throw new IllegalStateException("Could not autowire " + type.qualifiedName() + " as there is no active binding for it");
            else {
                final BoundContext<T, T> context = new ConstructorBoundContext<>(Key.of(type, Bindings.named("")), type);
                this.applicationContext.add(context);
                binding = Exceptional.of(context);
            }
        }
        final Exceptional<BoundContext<T, T>> finalBinding = binding;

        return Exceptional.of(() -> finalBinding.get().create(this.applicationContext, arguments)).orNull();
    }

    @Override
    public TypeFactory with(final Attribute<?>... properties) {
        final TypeFactoryImpl clone = this.applicationContext.get(TypeFactoryImpl.class);
        clone.properties = HartshornUtils.merge(this.properties, properties);
        return clone;
    }
}
