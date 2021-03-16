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

package org.dockbox.selene.command.parameter;

import org.dockbox.selene.annotations.command.CustomParameter;
import org.dockbox.selene.annotations.command.ParameterConstruction;
import org.dockbox.selene.api.command.source.CommandSource;
import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.util.Reflect;
import org.dockbox.selene.api.util.SeleneUtils;
import org.dockbox.selene.commandparameters.CommandParameterResources;
import org.dockbox.selene.commandparameters.exception.ConstructorDefinitionError;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;

public interface CustomParameterPattern {

    <T> Exceptional<T> request(Class<T> type, CommandSource source, String raw);

    default <T> Exceptional<Constructor<T>> getParameterConstructor(Class<T> type, int size) {
        @SuppressWarnings("unchecked") Constructor<T>[] ctors = (Constructor<T>[]) type.getDeclaredConstructors();
        if (ctors.length == 1) {
            return Exceptional.of(ctors[0]);
        }

        Collection<Constructor<T>> constructors = Reflect.getAnnotatedConstructors(ParameterConstruction.class, type);
        if (constructors.isEmpty()) {
            return Exceptional.of(new ConstructorDefinitionError("No constructors with @ParameterConstruction found for " + type.getCanonicalName()));
        }

        List<Constructor<T>> availableConstructors = SeleneUtils.emptyList();

        for (Constructor<T> declaredConstructor : constructors) {
            if (declaredConstructor.getParameters().length == size) {
                declaredConstructor.setAccessible(true);
                availableConstructors.add(declaredConstructor);
            }
        }

        if (availableConstructors.size() > 1)
            return Exceptional
                    .of(new ConstructorDefinitionError("Found more than one constructor with @ParamaterConstruction with " + size + " parameters for " + type
                            .getCanonicalName()));

        if (availableConstructors.isEmpty()) {
            return Exceptional.of(new IllegalArgumentException(CommandParameterResources.USAGE.format(type.getAnnotation(CustomParameter.class).usage()).asString()));
        }

        return Exceptional.of(availableConstructors.get(0));
    }
}
