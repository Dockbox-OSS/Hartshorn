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

package org.dockbox.hartshorn.commands.arguments;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.commands.CommandParameterResources;
import org.dockbox.hartshorn.commands.annotations.Parameter;
import org.dockbox.hartshorn.commands.arguments.ArgumentConverterRegistry;
import org.dockbox.hartshorn.commands.definition.ArgumentConverter;
import org.dockbox.hartshorn.commands.source.CommandSource;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * The type used to provide a argument pattern which can be used to construct types decorated with {@link Parameter}, typically done
 * through the {@link org.dockbox.hartshorn.commands.arguments.DynamicPatternConverter}, though this is not a requirement.
 */
public interface CustomParameterPattern {

    /**
     * Attempts to parse a {@code raw} argument into the requested {@code type}.
     *
     * @param type
     *         The target type to parse into
     * @param source
     *         The source of the command, provided in case the parser is context sensitive
     * @param raw
     *         The raw argument
     * @param <T>
     *         The generic type of the target
     *
     * @return An instance of {@code T}, wrapped in a {@link Exceptional}, or {@link Exceptional#empty()} if {@code null}
     */
    default <T> Exceptional<T> request(Class<T> type, CommandSource source, String raw) {
        Exceptional<Boolean> preconditionsMatch = this.preconditionsMatch(type, source, raw);
        if (preconditionsMatch.caught()) return Exceptional.of(preconditionsMatch.error());

        List<String> rawArguments = this.splitArguments(raw);
        List<Class<?>> argumentTypes = HartshornUtils.emptyList();
        List<Object> arguments = HartshornUtils.emptyList();

        for (String rawArgument : rawArguments) {
            Exceptional<String> argumentIdentifier = this.parseIdentifier(rawArgument);
            if (argumentIdentifier.absent()) {
                // If a non-pattern argument is required, the converter needs to be looked up by type instead of by its identifier. This will be done when the constructor is being looked up
                argumentTypes.add(null);
                arguments.add(rawArgument);
                continue;
            }
            String typeIdentifier = argumentIdentifier.get();

            ArgumentConverter<?> converter = ArgumentConverterRegistry.getConverter(typeIdentifier);
            if (converter == null) return Exceptional
                    .of(new IllegalArgumentException(Hartshorn.context().get(CommandParameterResources.class).getMissingConverter(type.getCanonicalName()).asString()));

            argumentTypes.add(converter.getType());
            arguments.add(converter.convert(source, rawArgument).orNull());
        }


        return this.getConstructor(argumentTypes, arguments, type, source).map(constructor -> {
            try {
                return constructor.newInstance(arguments.toArray(new Object[0]));
            }
            catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                return null;
            }
        });
    }

    <T> Exceptional<Boolean> preconditionsMatch(Class<T> type, CommandSource source, String raw);

    List<String> splitArguments(String raw);

    Exceptional<String> parseIdentifier(String argument);

    default <T> Exceptional<Constructor<T>> getConstructor(List<Class<?>> argumentTypes, List<Object> arguments, Class<T> type,
                                                           CommandSource source) {
        //noinspection unchecked
        for (Constructor<T> declaredConstructor : (Constructor<T>[]) type.getDeclaredConstructors()) {
            Class<?>[] parameterTypes = declaredConstructor.getParameterTypes();
            if (parameterTypes.length != arguments.size()) continue;

            boolean passed = true;
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> parameterType = parameterTypes[i];
                Class<?> requiredType = argumentTypes.get(i);
                if (requiredType == null) {
                    final ArgumentConverter<?> converter = ArgumentConverterRegistry.getConverter(parameterType);
                    Exceptional<?> result = converter.convert(source, (String) arguments.get(i));
                    if (result.present()) {
                        arguments.set(i, result.get());
                        continue; // Generic type, will be parsed later
                    }
                }
                else if (parameterType.equals(requiredType)) continue;

                passed = false;
                break; // Parameter is not what we expected, do not continue
            }
            if (passed) return Exceptional.of(declaredConstructor);
        }
        return Exceptional.of(new IllegalArgumentException(Hartshorn.context().get(CommandParameterResources.class).getNotEnoughArgs().asString()));
    }
}
