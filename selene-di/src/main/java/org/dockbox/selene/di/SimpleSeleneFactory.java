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

package org.dockbox.selene.di;

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.di.annotations.Named;
import org.dockbox.selene.di.binding.Bindings;
import org.dockbox.selene.di.inject.wired.WireContext;
import org.dockbox.selene.di.properties.BindingMetaProperty;
import org.dockbox.selene.di.properties.InjectableType;
import org.dockbox.selene.di.properties.InjectorProperty;
import org.dockbox.selene.util.SeleneUtils;

import javax.annotation.Nullable;

public class SimpleSeleneFactory implements SeleneFactory {

    private InjectorProperty<?>[] properties;

    public SimpleSeleneFactory() {
        this.properties = new InjectorProperty[0];
    }

    @Override
    public <T> T create(Class<T> type, Object... arguments) {
        @Nullable final InjectorProperty<Named> property = Bindings.property(BindingMetaProperty.KEY, Named.class, this.properties);
        Exceptional<WireContext<T, T>> binding = ApplicationContextAware.instance().getContext().firstWire(type, property);
        if (binding.absent()) throw new IllegalStateException("Could not autowire " + type.getCanonicalName() + " as there is no active binding for it");
        return Exceptional.of(() -> {
            final T instance = binding.get().create(arguments);
            if (instance instanceof InjectableType && ((InjectableType) instance).canEnable()) {
                ((InjectableType) instance).stateEnabling(this.properties);
            }
            return instance;
        }).orNull();
    }

    @Override
    public SeleneFactory with(InjectorProperty<?>... properties) {
        SimpleSeleneFactory clone = new SimpleSeleneFactory();
        clone.properties = SeleneUtils.merge(this.properties, properties);
        return clone;
    }
}
