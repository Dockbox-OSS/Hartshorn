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

import org.dockbox.selene.api.SeleneBootstrap;
import org.dockbox.selene.di.annotations.AutoWired;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class SimpleSeleneFactory implements SeleneFactory {

    public SimpleSeleneFactory() {
    }

    @Override
    public <T> T create(Class<T> type, Object... arguments) {
        Class<T> binding = SeleneBootstrap.getInstance().getBinding(type);
        if (binding == null) throw new IllegalStateException("Could not autowire " + type.getCanonicalName() + " as there is no active binding for it");

        Class<?>[] argumentTypes = Arrays.stream(arguments).map(Object::getClass).toArray(Class<?>[]::new);
        try {
            Constructor<T> constructor = binding.getConstructor(argumentTypes);
            if (constructor.isAnnotationPresent(AutoWired.class)) {
                return constructor.newInstance(arguments);
            }
            else {
                throw new IllegalArgumentException("Could not autowire " + type.getCanonicalName() + " as the applicable constructor is not marked with @AutoWired");
            }
        }
        catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException("Could not autowire " + type.getCanonicalName() + ", no constructor could be accessed");
        }
    }
}
