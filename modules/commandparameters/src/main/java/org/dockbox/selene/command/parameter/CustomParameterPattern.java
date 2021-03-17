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

/**
 * The type used to provide a argument pattern which can be used to construct types annotated with {@link CustomParameter}, typically done
 * through the {@link org.dockbox.selene.command.convert.DynamicPatternConverter}, though this is not a requirement.
 */
public interface CustomParameterPattern {

    /**
     * Attempts to parse a {@code raw} argument into the requested {@code type}.
     *
     * @param type The target type to parse into
     * @param source The source of the command, provided in case the parser is context sensitive
     * @param raw The raw argument
     * @param <T> The generic type of the target
     * @return An instance of {@code T}, wrapped in a {@link Exceptional}, or {@link Exceptional#empty()} if {@code null}
     */
    <T> Exceptional<T> request(Class<T> type, CommandSource source, String raw);

    /**
     * Looks up a constructor which can be used to construct a parameter instance. If there is only one constructor present that will
     * be returned even if it is not annotated with {@link ParameterConstruction}, as long as the amount of parameters matches the
     * expected amount. If there are more than one constructor present, only the constructors annotated with {@link ParameterConstruction}
     * are scanned.
     *
     * <p>It is expected that each constructor with this annotation has a different amount of parameters. Currently it is
     * not possible to have two constructors with the same amount of arguments, due to the unavailability of argument types during
     * construction.
     *
     * <p>Any {@link Exceptional} with a present {@link Throwable} returned by this method are to be handled by the developer, and
     * should be handled before it is returned to the user.
     *
     * @param type The type to scan for constructors
     * @param size The amount of parameters expected on the constructor
     * @param <T> The generic type of the declaring type
     * @return The constructor, wrapped in a {@link Exceptional}, or {@link Exceptional#empty()}
     */
    default <T> Exceptional<Constructor<T>> getParameterConstructor(Class<T> type, int size) {
        @SuppressWarnings("unchecked") Constructor<T>[] ctors = (Constructor<T>[]) type.getDeclaredConstructors();
        if (ctors.length == 1 && ctors[0].getParameterCount() == size) {
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
